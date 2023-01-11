/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.opennlp;


import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.nlp.AbstractNLPAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OpenNLPPOSTagger extends AbstractNLPAnnotator {
    private final static Logger logger = LogManager.getLogger(OpenNLPPOSTagger.class);

    private static final String annotatorName = "OpenNLP_POS";

    private final Language defaultLanguage;
    private final Map<Language, SentenceDetectorME> sentenceDetector;
    private final Map<Language, POSTaggerME> tagger;
    private final Map<Language, TokenizerME> tokenizer;

    private POSModel loadPOSModel(String resourceName) {
        try (InputStream modelIn = OpenNLPPOSTagger.class.getResourceAsStream(resourceName)) {
            return new POSModel(Objects.requireNonNull(modelIn));
        } catch (IOException e) {
            logger.error("Not able to load POS model {}", e.getMessage());
            throw new RuntimeException("Error initializing model");
        }
    }
    
    private SentenceModel loadSentenceModel(String resourceName) {
        try (InputStream modelIn = OpenNLPPOSTagger.class.getResourceAsStream(resourceName)) {
            return new SentenceModel(Objects.requireNonNull(modelIn));
        } catch (IOException e) {
            logger.error("Not able to load Sentence model {}", e.getMessage());
            throw new RuntimeException("Error initializing model");
        }
    }

    public OpenNLPPOSTagger(JsonNode configuration) {
        this.defaultLanguage = Language.valueOf(configuration.get("defaultLanguage").asText());
        this.sentenceDetector = new HashMap<>();
        this.tagger = new HashMap<>();
        this.tokenizer = new HashMap<>();
        
        Iterator<Map.Entry<String, JsonNode>> iterator = configuration.get("configuration").fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            JsonNode localConfiguration = entry.getValue();
            Language language = Language.valueOf(entry.getKey());

            if (localConfiguration.has("sentenceFinder")) {
                this.sentenceDetector.put(language, new SentenceDetectorME(loadSentenceModel(localConfiguration.get("sentenceFinder").asText())));
            }
            this.tokenizer.put(language, new TokenizerME(loadTokenizerModel(localConfiguration.get("tokenizer").asText())));
            this.tagger.put(language, new POSTaggerME(loadPOSModel(localConfiguration.get("tagger").asText())));
        }
    }

    private TokenizerModel loadTokenizerModel(String model) {
        try (InputStream modelStream = OpenNLPPOSTagger.class.getResourceAsStream(model)) {
            return new TokenizerModel(Objects.requireNonNull(modelStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error loading " + model);
    }

    @Override
    public List<IdentifiedEntity> identify(String text, Language language) {
        List<IdentifiedEntity> results = new ArrayList<>();

        if (language == Language.UNKNOWN) {
            language = this.defaultLanguage;
        }

        final SentenceDetectorME sentenceDetector = this.sentenceDetector.get(language);

        final Span[] sentenceSpans;
        if (sentenceDetector == null) {
            logger.info("Sentence detection for language {} is missing", language.name());
            sentenceSpans = new Span[] {
                    new Span(0, text.length())
            };
        } else {
            synchronized (this.sentenceDetector) {
                sentenceSpans = sentenceDetector.sentPosDetect(text);
            }
        }

        for (Span sentenceSpan : sentenceSpans) {
            String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());

            final String[] tokens;
            final Span[] tokenPositionsWithinSentence;

            synchronized (tokenizer) {
                tokens = tokenizer.get(language).tokenize(sentence);
                tokenPositionsWithinSentence = tokenizer.get(language).tokenizePos(sentence);
            }
            final String[] tags;

            synchronized (tagger) {
                tags = tagger.get(language).tag(tokens);
            }
            
            int sentenceStart = sentenceSpan.getStart();
            
            for(int i = 0; i < tokens.length; i++) {
                int beginningOfFirstWord = tokenPositionsWithinSentence[i].getStart();
                int endOfLastWord = tokenPositionsWithinSentence[i].getEnd();
                
                int startOffsetInDoc = sentenceStart + beginningOfFirstWord;
                int endOffsetInDoc = sentenceStart + endOfLastWord;
                
                String nameInDocument = text.substring(startOffsetInDoc, endOffsetInDoc);
                
                IdentifiedEntity entity = new IdentifiedEntity(nameInDocument, startOffsetInDoc, endOffsetInDoc,
                        Collections.emptySet(),
                        Collections.singleton(PartOfSpeechType.valueOf(tags[i])));
                
                results.add(entity);
            }
        }
        
        return results;
    }

    @Override
    public String getName() {
        return annotatorName;
    }
}
