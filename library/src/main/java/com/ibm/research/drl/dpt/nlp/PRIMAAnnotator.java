/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierWithOffset;
import com.ibm.research.drl.dpt.util.JsonUtils;
import com.ibm.research.drl.dpt.util.Tuple;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PRIMAAnnotator extends AbstractNLPAnnotator implements Serializable {
    private static final Logger logger = LogManager.getLogger(PRIMAAnnotator.class);

    private static final String annotatorName = "PRIMA";
    private static final Pattern trailingPunctuation = Pattern.compile("\\p{Punct}+\\s*$");

    private final int MIN_SHINGLE_SIZE;
    private final int MAX_SHINGLE_SIZE;
    private final Map<String, String> typeMap;
    private final boolean splitSentences;
    private final IdentifierFactory identifierFactory;
    private final List<Pattern> customSentenceSplitPatterns;

    private transient TokenizerME tokenizer;
    private transient SentenceDetectorME sentenceDetector;

    private final String sentenceModelReference;
    private final String tokenizerModelReference;
    private final int minSpanSize;

    private final boolean performPOSTagging = false;

    public PRIMAAnnotator(JsonNode properties) {
        this(properties, null);
    }

    public PRIMAAnnotator(JsonNode properties, IdentifierFactory identifierFactory) {
        this.typeMap = extractMapping(properties.get("mapping"));
        this.MIN_SHINGLE_SIZE = properties.get("MIN_SHINGLE_SIZE").asInt(2);
        this.MAX_SHINGLE_SIZE = properties.get("MAX_SHINGLE_SIZE").asInt(10);
        this.splitSentences = properties.get("splitSentences").asBoolean();
        final Set<String> customSentenceSplitAbbreviations = JsonUtils.setFromArrayOfStrings(properties.get("customSentenceSplitAbbreviations"));
        this.customSentenceSplitPatterns = buildSplitPatterns(customSentenceSplitAbbreviations);

        this.sentenceModelReference = properties.get("sentenceDetectorModel").asText();
        this.tokenizerModelReference = properties.get("tokenizerModel").asText("/nlp/en/en-token.bin");

        this.sentenceDetector = buildSentenceDetector();
        this.tokenizer = buildTokenizer();
        this.minSpanSize = Optional.ofNullable(properties.get("minSpanSize")).orElse(NullNode.getInstance()).asInt(3);

        if (null == identifierFactory) {
            if (properties.has("identifiers")) {
                logger.info("Loading custom list of identifiers");
                this.identifierFactory = IdentifierFactory.initializeIdentifiers(properties.get("identifiers"));
            } else {
                logger.error("No identifiers provided");
                throw new RuntimeException("No identifiers provided");
            }
        } else {
            logger.info("Using provided identifier factory");
            this.identifierFactory = identifierFactory;
        }
    }

    private SentenceDetectorME buildSentenceDetector() {
        try (InputStream sentenceDetectorModel = PRIMAAnnotator.class.getResourceAsStream(sentenceModelReference)) {
            return new SentenceDetectorME(new SentenceModel(Objects.requireNonNull(sentenceDetectorModel)));
        } catch (IOException e) {
            logger.debug("Error creating sentence tokenizer model", e);
            throw new RuntimeException("Error creating sentence tokenizer model");
        }
    }

    private TokenizerME buildTokenizer() {
        try (InputStream inputStream = PRIMAAnnotator.class.getResourceAsStream(tokenizerModelReference)) {
            return new TokenizerME(new TokenizerModel(Objects.requireNonNull(inputStream)));
        } catch (IOException e) {
            logger.debug("Error creating tokenizer model", e);
            throw new RuntimeException("Error creating tokenizer model");
        }
    }

    private List<Pattern> buildSplitPatterns(Set<String> customSentenceSplitAbbreviations) {
        List<Pattern> patterns = new ArrayList<>();
        
        for(String pattern: customSentenceSplitAbbreviations) {
            patterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
        
        return patterns;
    }

    private List<Tuple<Set<IdentifiedEntityType>, Tuple<Integer, Integer>>> detectTypes(String term, boolean posOK) {
        logger.debug("Detecting types for \"{}\" with POS information decided as {}", term, posOK);
        final Collection<Identifier> identifiers = identifierFactory.availableIdentifiers();
        
        final List<Tuple<Set<IdentifiedEntityType>, Tuple<Integer, Integer>>> finalResults = new ArrayList<>();
        final Set<IdentifiedEntityType> identifiedTypes = new HashSet<>(identifiers.size());

        for (final Identifier identifier: identifiers) {
            if (!identifier.isPOSIndependent() && !posOK) {
                continue;
            }
            
            if (identifier instanceof IdentifierWithOffset) {
                Tuple<Boolean, Tuple<Integer, Integer>> result = ((IdentifierWithOffset) identifier).isOfThisTypeWithOffset(term);
                if (result.getFirst()) {
                    Integer offset = result.getSecond().getFirst();
                    Integer depth = result.getSecond().getSecond();
                    
                    final String identifierName = identifier.getType().getName();
                    final String type = mapType(identifierName);

                    IdentifiedEntityType identifiedEntityType = new IdentifiedEntityType(type, identifierName, annotatorName);
                    finalResults.add(new Tuple<>(Collections.singleton(identifiedEntityType), new Tuple<>(offset, depth))); 
                }
            } else {
                if (identifier.isOfThisType(term)) {
                    final String identifierName = identifier.getType().getName();
                    final String type = mapType(identifierName);

                    identifiedTypes.add(new IdentifiedEntityType(type, identifierName, annotatorName));
                }
            }
        }

        finalResults.add(new Tuple<>(identifiedTypes, new Tuple<>(0, term.length()))); 
        
        return finalResults;
    }

    private String mapType(String type) {
        return typeMap.getOrDefault(type, type);
    }

    private boolean checkPOS(Set<PartOfSpeechType> pos) {
        if (Objects.nonNull(pos) && !pos.isEmpty()) {
            logger.debug("Check if POS is not modal, verb, adverb or pronoun: {}", pos);

            for (PartOfSpeechType p : pos) {
                if (p.isModal() || p.isVerb() || p.isAdverb() || p.isPronoun()) {
                    return false;
                }
            }
        } else {
            logger.debug("Part of speech information not available");
        }
        
        return true;
    }

    private List<IdentifiedEntity> identifySentence(String sentence, int offset, String text, Language language) {
        List<IdentifiedEntity> identifiedEntities = new ArrayList<>();

        sentence = removeTrailingPunctuation(sentence);


        final Span[] tokenPositionsWithinSentence;
        final String[] tokens;
        final String[] tags;

        synchronized (tokenizer) {
            tokenPositionsWithinSentence = tokenizer.tokenizePos(sentence).clone();

            tokens = tokenizeForPOSIfRequired(sentence);
            tags = applyPosTagIfRequired(tokens);
        }

        PrimaShingleGenerator shingleGenerator = new PrimaShingleGenerator(MIN_SHINGLE_SIZE, MAX_SHINGLE_SIZE);

        for (PrimaShingle nextShingle : shingleGenerator.generate(tokenPositionsWithinSentence)) {
            final Span span = nextShingle.getSpan();
            final Set<PartOfSpeechType> pos = extractPosTagging(tokens, tokenPositionsWithinSentence, span, tags);

            if (span.length() < minSpanSize) continue;

            List<Tuple<Set<IdentifiedEntityType>, Tuple<Integer, Integer>>> detectedTypesWithOffset = detectTypes(
                    extractTextToIdentify(text, offset, span),
                    checkPOS(pos));

            for (Tuple<Set<IdentifiedEntityType>, Tuple<Integer, Integer>> entry : detectedTypesWithOffset) {
                Set<IdentifiedEntityType> detectedTypes = entry.getFirst();
                int offsetWithinEntity = entry.getSecond().getFirst();
                int depthWithinEntity = entry.getSecond().getSecond();

                if (!detectedTypes.isEmpty()) {
                    int start = span.getStart() + offset + offsetWithinEntity;
                    int end = start + depthWithinEntity;

                    identifiedEntities.add(
                            new IdentifiedEntity(
                                    extractOriginalText(text, start, end),
                                    start,
                                    end,
                                    detectedTypes,
                                    pos
                            )
                    );
                }
            }
        }
        return mergeOverlappingSingleType(identifiedEntities);
    }

    private String[] tokenizeForPOSIfRequired(String sentence) {
        if (performPOSTagging) {
            return tokenizer.tokenize(sentence);
        }
        return new String[0];
    }

    private String[] applyPosTagIfRequired(String[] tokens) {
        return new String[0];
    }

    private Set<PartOfSpeechType> extractPosTagging(String[] tokens, Span[] tokenPositionsWithinSentence, Span span, String[] tags) {
        if (performPOSTagging) {
            Set<PartOfSpeechType> pos = new HashSet<>();

            for (int i = 0; i < tokens.length; i++) {
                int beginning = tokenPositionsWithinSentence[i].getStart();

                if (beginning >= span.getStart() && beginning < span.getEnd()) {
                    pos.add(PartOfSpeechType.valueOf(tags[i]));
                }

                if (beginning >= span.getEnd()) {
                    break;
                }
            }

            return pos;
        }
        return Collections.emptySet();
    }

    private String extractTextToIdentify(String text, int offset, Span nextTermSpan) {
        String toBeProcessed =  text.substring(
                nextTermSpan.getStart() + offset,
                nextTermSpan.getEnd() + offset
        );

        final Matcher matcher = trailingPunctuation.matcher(toBeProcessed);
        if (matcher.find()) {
            toBeProcessed = toBeProcessed.substring(0, matcher.start());
        }

        return toBeProcessed;
    }

    private List<IdentifiedEntity> mergeOverlappingSingleType(List<IdentifiedEntity> entityList) {
        if (entityList.isEmpty()) return entityList;

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));

        int j = 0;
        IdentifiedEntity t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getEnd() == t2.getEnd() && sameType(t1, t2)) {
                // merge
                final Set<IdentifiedEntityType> types = t1.getType();
                final Set<PartOfSpeechType> pos = t1.getPos();

                IdentifiedEntity newT1;

                if (t1.getStart() <= t2.getStart()) {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, pos);
                } else {
                    newT1 = new IdentifiedEntity(t2.getText(), t2.getStart(), t1.getEnd(), types, pos);
                }

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        j = 0;
        t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getStart() == t2.getStart() && sameType(t1, t2)) {
                // merge
                final Set<IdentifiedEntityType> types = t1.getType();
                final Set<PartOfSpeechType> pos = t1.getPos();

                IdentifiedEntity newT1;

                if (t1.getEnd() < t2.getEnd()) {
                    newT1 = new IdentifiedEntity(t2.getText(), t1.getStart(), t2.getEnd(), types, pos);
                } else {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, pos);
                }

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        j = 0;
        t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getStart() <= t2.getStart() && t1.getEnd() >= t2.getEnd() && sameType(t1, t2)) {
                // merge
                final Set<IdentifiedEntityType> types = t1.getType();

                IdentifiedEntity newT1;

                newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, t1.getPos());

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        return entityList;
    }

    private boolean sameType(IdentifiedEntity entity1, IdentifiedEntity entity2) {
        Set<IdentifiedEntityType> types1 = entity1.getType();
        Set<IdentifiedEntityType> types2 = entity2.getType();

        return types1.containsAll(types2) && types2.containsAll(types1);
    }

    /* hardcoded change dot to space */
    private List<Integer> normalizeTextChanges(String text, List<Pattern> patterns) {
        if (patterns.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Integer> changes = new ArrayList<>();
        
        for(Pattern pattern: patterns) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                for(int i = matcher.start(); i < matcher.end(); i++) {
                    if (text.charAt(i) == '.') {
                        changes.add(i);
                    }
                }
            }

        }
        
        return changes;
    }
    
    private String applyDotChanges(String text, List<Integer> changes) {
        if (changes.isEmpty()) {
            return text;
        }
        
        StringBuilder builder = new StringBuilder();
        int lastOffset = 0;
        
        Collections.sort(changes);
        
        for(Integer change: changes) {
            builder.append(text, lastOffset, change);
            builder.append(' ');
            lastOffset = change + 1;
        }
        
        builder.append(text.substring(lastOffset));

        return builder.toString();
    }
    
    @Override
    public List<IdentifiedEntity> identify(String text, Language language) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<IdentifiedEntity> identifiedEntities = new ArrayList<>();
       
        if (this.splitSentences) {
            List<Integer> changes = normalizeTextChanges(text, this.customSentenceSplitPatterns);
            String normalizedText = applyDotChanges(text, changes);

            final Span[] sentenceSpans;
            synchronized (sentenceDetector) {
                sentenceSpans = sentenceDetector.sentPosDetect(normalizedText);
            }
            for (Span sentenceSpan : sentenceSpans) {
                String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
                int offset = sentenceSpan.getStart();
                identifiedEntities.addAll(identifySentence(sentence, offset, text, language));
            }
        } else {
            identifiedEntities.addAll(identifySentence(text, 0, text, language));
        }

        return identifiedEntities;
    }


    private String removeTrailingPunctuation(String term) {
        final Matcher matcher = trailingPunctuation.matcher(term);

        if (matcher.find()) {
            return term.substring(0, matcher.start());
        } else {
            return term;
        }
    }

    private String extractOriginalText(String text, int start, int end) {
        return text.substring(start, end);
    }

    @Override
    public String getName() {
        return annotatorName;
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        this.sentenceDetector = buildSentenceDetector();
        this.tokenizer = buildTokenizer();
    }

    public IdentifierFactory getIdentifierFactory() {
        return this.identifierFactory;
    }

    @Override
    public List<String> getPosIndependentTypes() {
        return identifierFactory.availableIdentifiers().stream()
                .filter( Identifier::isPOSIndependent)
                .map(Identifier::getType).map(ProviderType::getName)
                .map(this::mapType)
                .collect(Collectors.toList());
    }
}
