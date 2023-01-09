/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.opennlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.nlp.AbstractNLPAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntityType;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import com.ibm.research.drl.dpt.providers.ProviderType;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OpenNLPAnnotator extends AbstractNLPAnnotator {
    private final static String annotatorName = "OpenNLP";
    
    private final Map<Language, NameFinderME> nameFinder;
    private final Map<Language, NameFinderME> timeFinder;
    private final Map<Language, NameFinderME> dateFinder;
    private final Map<Language, NameFinderME> locationFinder;
    private final Map<Language, NameFinderME> organizationFinder;
    private final Map<Language, SentenceDetectorME> sentenceDetector;
    private final Map<Language, TokenizerME> tokenizer;
    private final Language defaultLanguage;

    private TokenNameFinderModel loadNameModel(String resourceName) {
        try (InputStream modelIn = OpenNLPAnnotator.class.getResourceAsStream(resourceName)) {
            return new TokenNameFinderModel(Objects.requireNonNull(modelIn));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("error initializing model from " + resourceName);
    }

    private SentenceModel loadSentenceModel(String resourceName) {
        try (InputStream modelIn = OpenNLPAnnotator.class.getResourceAsStream(resourceName)) {
            return new SentenceModel(Objects.requireNonNull(modelIn));
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("error initializing model");
    }
    
    public OpenNLPAnnotator(JsonNode configuration) {
        this.defaultLanguage = Language.valueOf(configuration.get("defaultLanguage").asText());
        this.dateFinder = new HashMap<>();
        this.nameFinder = new HashMap<>();
        this.locationFinder = new HashMap<>();
        this.organizationFinder = new HashMap<>();
        this.timeFinder = new HashMap<>();
        this.sentenceDetector = new HashMap<>();
        this.tokenizer = new HashMap<>();
        
        Iterator<Map.Entry<String, JsonNode>> iterator = configuration.get("configuration").fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            JsonNode localConfiguration = entry.getValue();
            Language language = Language.valueOf(entry.getKey());

            this.dateFinder.put(language, new NameFinderME(loadNameModel(localConfiguration.get("dateFinder").asText())));
            this.nameFinder.put(language, new NameFinderME(loadNameModel(localConfiguration.get("nameFinder").asText())));
            this.locationFinder.put(language, new NameFinderME(loadNameModel(localConfiguration.get("locationFinder").asText())));
            this.organizationFinder.put(language, new NameFinderME(loadNameModel(localConfiguration.get("organizationFinder").asText())));
            this.timeFinder.put(language, new NameFinderME(loadNameModel(localConfiguration.get("timeFinder").asText())));
            this.sentenceDetector.put(language, new SentenceDetectorME(loadSentenceModel(localConfiguration.get("sentenceFinder").asText())));
            this.tokenizer.put(language, new TokenizerME(loadTokenizerModel(localConfiguration.get("tokenizer").asText())));
        }
        
    }

    private TokenizerModel loadTokenizerModel(String model) {
        try (InputStream modelStream = OpenNLPAnnotator.class.getResourceAsStream(model)) {
            return new TokenizerModel(Objects.requireNonNull(modelStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error loading " + model);
    }


    private List<IdentifiedEntity> processSpans(String text, Span[] tokens, 
                                                Span[] detected, int sentenceStart, ProviderType providerType) {
        List<IdentifiedEntity> results = new ArrayList<>();

        for(Span span: detected) {
            int beginningOfFirstWord = tokens[span.getStart()].getStart();
            int endOfLastWord = tokens[span.getEnd() - 1].getEnd();
            
            int startOffsetInDoc = sentenceStart + beginningOfFirstWord;
            int endOffsetInDoc = sentenceStart + endOfLastWord;

            String nameInDocument = text.substring(startOffsetInDoc, endOffsetInDoc);
            
            IdentifiedEntity entity = new IdentifiedEntity(nameInDocument, startOffsetInDoc, endOffsetInDoc, 
                    Collections.singleton(new IdentifiedEntityType(providerType.getName(), providerType.getName(), annotatorName)), Collections.singleton(PartOfSpeechType.UNKNOWN));
            
            results.add(entity);
        }
        
        return results;
    }

    @Override
    public List<IdentifiedEntity> identify(String text, Language language) throws IOException {
        List<IdentifiedEntity> results = new ArrayList<>();
      
        if (language == Language.UNKNOWN) {
            language = this.defaultLanguage;
        }
        
        SentenceDetectorME sentenceDetector = this.sentenceDetector.get(language);
        if (sentenceDetector == null) {
            throw new RuntimeException("configuration for language " + language.name() + " is missing");
        }

        final Span[] sentenceSpans = sentenceDetector.sentPosDetect(text);

        for (Span sentenceSpan : sentenceSpans) {
            String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
            
            String[] tokens = tokenizer.get(language).tokenize(sentence);
            Span[] tokenPositionsWithinSentence = tokenizer.get(language).tokenizePos(sentence);
            
            Span[] nameSpans = nameFinder.get(language).find(tokens);
            Span[] locationSpans = locationFinder.get(language).find(tokens);
            Span[] orgSpans = organizationFinder.get(language).find(tokens);
            Span[] timeSpans = timeFinder.get(language).find(tokens);
            Span[] dateSpans = dateFinder.get(language).find(tokens);

            
            int sentenceStart = sentenceSpan.getStart();
            results.addAll(processSpans(text, tokenPositionsWithinSentence, nameSpans, sentenceStart, ProviderType.NAME));
            results.addAll(processSpans(text, tokenPositionsWithinSentence, locationSpans, sentenceStart, ProviderType.ADDRESS));
            results.addAll(processSpans(text, tokenPositionsWithinSentence, orgSpans, sentenceStart, ProviderType.ORGANIZATION));
            results.addAll(processSpans(text, tokenPositionsWithinSentence, timeSpans, sentenceStart, ProviderType.DATETIME));
            results.addAll(processSpans(text, tokenPositionsWithinSentence, dateSpans, sentenceStart, ProviderType.DATETIME));
            
        }
        
        nameFinder.get(language).clearAdaptiveData();
        locationFinder.get(language).clearAdaptiveData();
        timeFinder.get(language).clearAdaptiveData();
        dateFinder.get(language).clearAdaptiveData();
        organizationFinder.get(language).clearAdaptiveData();
        
        return results;
    }

    @Override
    public String getName() {
        return annotatorName;
    }
}
