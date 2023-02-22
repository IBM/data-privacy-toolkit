/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.nlp.opennlp.OpenNLPLanguageDetector;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ComplexFreeTextAnnotator extends AbstractNLPAnnotator {
    private final static Logger logger = LogManager.getLogger(ComplexFreeTextAnnotator.class);

    private final LanguageDetector languageDetector;
    private final List<NLPAnnotator> nlpAnnotators;
    private final Map<String, Set<String>> blacklistedValues;
    private final Map<String, List<Identifier>> blacklistIdentifierBased;
    private final Map<String, Set<String>> unreliableSource;
    private final List<ConnectedEntities> connectedEntities;
    private final String defaultLanguage;
    private final boolean performLanguageDetection;

    private final Map<String, Map<String, Double>> weights;

    private final Map<String, Set<String>> posIndependent;
    private final Set<String> toNotToBeReported;

    public ComplexFreeTextAnnotator(JsonNode configuration, NLPAnnotator ... nlpAnnotators) {
        if (null != nlpAnnotators && nlpAnnotators.length != 0) {
            this.nlpAnnotators = Arrays.asList(nlpAnnotators);
        } else if ( configuration.has("identifiers")) {
            this.nlpAnnotators = constructIdentifiers(configuration.get("identifiers"));
        } else {
            logger.error("No NLP annotator available");
            throw new RuntimeException("No NLP annotator available");
        }
        this.blacklistedValues = buildBlacklist(configuration.get("blacklist"));
        this.blacklistIdentifierBased = buildBlackListIdentifierBased(configuration.get("blacklist"));
        this.unreliableSource = extractUnreliableSources(configuration.get("unreliable"));
        this.posIndependent = extractPostIndependent(configuration.get("not_POS"));

        enrichPosInformationFromAnnotators(posIndependent, this.nlpAnnotators);

        this.connectedEntities = extractConnectedEntities(configuration.get("connected"));
        this.weights = extractWeights(configuration.get("weights"));
        this.toNotToBeReported = extractToNotToBeReported(configuration.get("doNotReport"));

        this.defaultLanguage = configuration.get("defaultLanguage").asText();
        this.performLanguageDetection = configuration.get("performLanguageDetection").asBoolean();
        if (performLanguageDetection) {
            this.languageDetector = new OpenNLPLanguageDetector();
        } else {
            this.languageDetector = null;
        }
    }

    private Map<String, List<Identifier>> buildBlackListIdentifierBased(JsonNode blacklist) {
        final Map<String, List<Identifier>> map = new HashMap<>();

        blacklist.fields().forEachRemaining( entry -> {
            final String providerType = entry.getKey();
            final List<Identifier> identifierList = new ArrayList<>();

            entry.getValue().elements().forEachRemaining( element -> {
                String term = element.asText();

                if (term.startsWith("{{") && term.endsWith("}}")) {
                    String fullyQualifiedClassName = term.substring(2, term.length() - 2).trim();
                    identifierList.add(createInstance(fullyQualifiedClassName));
                }
            });

            map.putIfAbsent(providerType, identifierList);
        } );

        return map;
    }

    private List<NLPAnnotator> constructIdentifiers(JsonNode identifiers) {
        if (identifiers.isObject()) {
            List<NLPAnnotator> identifiersFromConfiguration = new ArrayList<>();

            identifiers.fields().forEachRemaining( identifier -> {

                try {
                    Class<?> classReference =  Class.forName(identifier.getKey());
                    @SuppressWarnings("unchecked")
                    Class<? extends NLPAnnotator> identifierClass = (Class<? extends NLPAnnotator>) classReference;
                    Constructor<? extends NLPAnnotator> constructor = identifierClass.getConstructor(JsonNode.class);

                    NLPAnnotator identifierInstance = constructor.newInstance(identifier.getValue());

                    identifiersFromConfiguration.add(identifierInstance);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error initializing " + identifier.getKey());
                }
            });

            return identifiersFromConfiguration;
        } else {
            throw new RuntimeException("Missing nlpAnnotators section");
        }
    }

    private void enrichPosInformationFromAnnotators(Map<String, Set<String>> posIndependent, List<NLPAnnotator> nlpAnnotators) {
        nlpAnnotators.stream().flatMap(annotator -> annotator.getPosIndependentTypes().stream())
                .distinct().forEach(type -> posIndependent.putIfAbsent(type, Collections.emptySet()));
    }

    private Set<String> extractToNotToBeReported(JsonNode doNotReport) {
        if (null == doNotReport || doNotReport.isNull()) return Collections.emptySet();

        Set<String> doNotReportElements = new HashSet<>();
        doNotReport.elements().forEachRemaining( element -> {
            String name = element.asText();
            doNotReportElements.add(name);
        });

        return doNotReportElements;
    }

    private Map<String, Map<String, Double>> extractWeights(JsonNode weights) {
        if (null == weights || weights.isNull()) return Collections.emptyMap();

        Map<String, Map<String, Double>> weightsMap = new HashMap<>();

        weights.fields().forEachRemaining((entry) -> {
            String annotatorName = entry.getKey();
            if (isNotValidName(annotatorName)) {
                throw new RuntimeException("Invalid annotator name: " + annotatorName);
            }

            Map<String, Double> annotatorWeights = buildAnnotatorWeightMap(entry.getValue());

            weightsMap.put(annotatorName, annotatorWeights);
        });

        return weightsMap;
    }

    private boolean isNotValidName(String annotatorName) {
        for (NLPAnnotator identifier : this.nlpAnnotators) {
            if (identifier.getName().equals(annotatorName)) return false;
        }
        return true;
    }

    private Map<String, Double> buildAnnotatorWeightMap(JsonNode annotatorWeights) {
        if (null == annotatorWeights || annotatorWeights.isNull()) return Collections.emptyMap();

        Map<String, Double> weights = new HashMap<>();

        annotatorWeights.fields().forEachRemaining((entry) -> {
            String type = entry.getKey();
            double weight = entry.getValue().asDouble(1.0);

            weights.put(type, weight);
        });

        return weights;
    }

    private List<ConnectedEntities> extractConnectedEntities(JsonNode connected) {
       List<ConnectedEntities> connectedEntities = new ArrayList<>();

       connected.elements().forEachRemaining(element -> {
           final String first = element.get("first").asText();
           final String second = element.get("second").asText();
           final String endType = element.get("endType").asText();
           final String endSubtype = element.get("endSubtype").asText();

           Set<String> particles = new HashSet<>();

           element.get("particles").elements().forEachRemaining(particle -> particles.add(particle.asText()));

           connectedEntities.add(new ConnectedEntities(first, second, endType, endSubtype, particles));
       });

       return connectedEntities;
    }

    private Map<String, Set<String>> extractUnreliableSources(JsonNode configuration) {
        Map<String,Set<String>> unreliable = new HashMap<>();

        configuration.fields().forEachRemaining(field -> {
            String annotatorName = field.getKey();

            if (isNotValidName(annotatorName)) {
                throw new RuntimeException("Invalid annotator name: " + annotatorName);
            }

            Set<String> types = new HashSet<>();

            field.getValue().elements().forEachRemaining( type -> types.add(type.asText()));

            unreliable.put(annotatorName, types);
        });

        return unreliable;
    }

    @Override
    public List<IdentifiedEntity> identify(String text, Language language, NLPAnnotator... customIdentifier) {
        Language detectedLanguage = (this.performLanguageDetection) ? this.languageDetector.detectLanguage(text) : Language.valueOf(this.defaultLanguage);

        Stream<NLPAnnotator> identifiersStream = nlpAnnotators.stream();
        if (null != customIdentifier && customIdentifier.length != 0) {
            identifiersStream = Stream.concat(identifiersStream, Arrays.stream(customIdentifier));
        }

        List<IdentifiedEntity> mergedList = identifiersStream.map(identifier -> {
            try {
                return identifier.identify(text, detectedLanguage);
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }

            return null;
        })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(this::isEntityValid)
                .map(this::removeUnreliableSources)
                .collect(Collectors.toList());

        List<IdentifiedEntity> resolvedEntities = splitAndMergeOverlapping(mergedList).stream()
                .map(this::resolveType)
                .filter(this::filterIncorrectIdentifications)
                .collect(Collectors.toList());

        List<IdentifiedEntity> listOfIdentified = mergeAdjacentEntities(resolvedEntities, text).stream()
                .filter(this::filterIncorrectIdentifications)
                .map(entity -> new IdentifiedEntity(entity.getText(),
                        entity.getStart(),
                        entity.getEnd(),
                        entity.getType().stream().filter(newCheckBlackList(entity.getText())).collect(Collectors.toSet()),
                        entity.getPos()))
                .filter(((Predicate<IdentifiedEntity>) entity -> entity.getType().isEmpty()).negate())
                .collect(Collectors.toList());

        return adjustOffsets(
                filterToNotBeReported(mergeEntityListsAndOverlappingEntities(
                        identifyMissedRepetitions(
                                mergeConnectedEntities(
                                        mergeAdjacentEntities(listOfIdentified, text),
                                        text),
                                text, false))));
    }

    private List<IdentifiedEntity> adjustOffsets(List<IdentifiedEntity> identifiedEntities) {
        return identifiedEntities.stream().map(
                entity -> {
                    String text = entity.getText();
                    String cleanedText = text.strip();

                    if (text.equals(cleanedText)) return entity;

                    int start = entity.getStart() + text.indexOf(cleanedText);

                    return new IdentifiedEntity(
                            cleanedText,
                            start,
                            start + cleanedText.length(),
                            entity.getType(),
                            entity.getPos()
                    );
                }
        ).collect(Collectors.toList());
    }

    private boolean isEntityValid(IdentifiedEntity identifiedEntity) {
        int expectedSize = identifiedEntity.getEnd() - identifiedEntity.getStart();
        return expectedSize != 0 && identifiedEntity.getText().length() == expectedSize;
    }

    private Predicate<IdentifiedEntityType> newCheckBlackList(String text) {
        return identifiedEntityType -> {
            final Set<String> defaults = this.blacklistedValues.getOrDefault("*", Collections.emptySet());
            final List<Identifier> defaultIdentifiers = this.blacklistIdentifierBased.getOrDefault("*", Collections.emptyList());

            final String providerType = identifiedEntityType.getType();

            final Set<String> values = this.blacklistedValues.getOrDefault(providerType, defaults);

            if (values.contains(text)) {
                return false;
            }

            List<Identifier> identifierList = this.blacklistIdentifierBased.getOrDefault(providerType, defaultIdentifiers);

            for (Identifier identifier : identifierList) {
                if (identifier.isOfThisType(text)) {
                    return false;
                }
            }

            return true;
        };
    }

    private List<IdentifiedEntity> filterToNotBeReported(List<IdentifiedEntity> identifiedEntities) {
        return identifiedEntities.stream().filter(
                entity -> {
                    IdentifiedEntityType type = entity.getType().iterator().next();
                    return !toNotToBeReported.contains(type.getType());
                }
        ).collect(Collectors.toList());
    }

    public List<IdentifiedEntity> identifyMissing(List<IdentifiedEntity> identifiedEntities,
                                                  String text, Language language) {
        return identifyMissedRepetitions(identifiedEntities, text, true);
    }

    private List<IdentifiedEntity> mergeAdjacentEntities(List<IdentifiedEntity> identifiedEntities, String text) {
        identifiedEntities.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        identifiedEntities.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        final List<IdentifiedEntity> mergedIdentifiedEntities = new ArrayList<>();

        for (int i = 0; i < identifiedEntities.size(); ) {
            IdentifiedEntity first = identifiedEntities.get(i);

            int j = i + 1;
            for(; j < identifiedEntities.size(); ++j) {
                final IdentifiedEntity second = identifiedEntities.get(j);

                if (notOfTheSameType(first, second) || notSeparatedOnlyByWhitespace(first, second, text)) break;

                int from = Math.min(first.getStart(), second.getStart());
                int to = Math.max(first.getEnd(), second.getEnd());

                first = new IdentifiedEntity(
                    text.substring(from, to),
                        from,
                        to,
                        first.getType(),
                        mergePos(first.getPos(), second.getPos())
                );
            }

            mergedIdentifiedEntities.add(first);
            i = j;
        }

        return mergedIdentifiedEntities;
    }

    private boolean notSeparatedOnlyByWhitespace(IdentifiedEntity first, IdentifiedEntity second, String text) {
        String textInBetween = text.substring(first.getEnd(), second.getStart());

        return textInBetween.chars().anyMatch(
                ((IntPredicate) Character::isWhitespace).negate()
        );
    }

    private boolean notOfTheSameType(IdentifiedEntity first, IdentifiedEntity second) {
        return !first.getType().containsAll(second.getType()) || !second.getType().containsAll(first.getType());
    }

    private List<IdentifiedEntity> sortEndStart(List<IdentifiedEntity> list) {
        list.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        list.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        return list;
    }

    protected List<IdentifiedEntity> splitAndMergeOverlapping(List<IdentifiedEntity> entities) {
        return merge(split(entities));
    }

    protected List<IdentifiedEntity> split(List<IdentifiedEntity> entities) {
        if (entities.size() == 1) return entities;

        entities.sort(Comparator.comparingInt(IdentifiedEntity::getStart));
        entities.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));

        final List<IdentifiedEntity> splits = new ArrayList<>();

        final List<Integer> splitPoints = findAllSplitPoints(entities);

        for (IdentifiedEntity entity : entities) {
            List<IdentifiedEntity> entitySplits = computeEntitySplits(entity, splitPoints);

            splits.addAll(entitySplits);
        }

        return splits;
    }

    private List<Integer> findAllSplitPoints(List<IdentifiedEntity> entities) {
        return entities.stream()
                .flatMapToInt(entity -> Arrays.stream(new int[]{entity.getStart(), entity.getEnd()}))
                .sorted()
                .distinct()
                .boxed()
                .collect(Collectors.toList());
    }

    private List<IdentifiedEntity> computeEntitySplits(final IdentifiedEntity entity, List<Integer> sortedSplitPoint) {
        int lastSplit = entity.getStart();

        final String entityText = entity.getText();

        final List<IdentifiedEntity> entitySplits = new ArrayList<>();

        for (int splitPoint : sortedSplitPoint) {
            if (splitPoint <= entity.getStart()) continue;
            if (splitPoint >= entity.getEnd()) break;

            final int start = lastSplit - entity.getStart();
            final int end = start + (splitPoint - lastSplit);

            final String text = entityText.substring(start, end);

            if (!text.isEmpty() && !text.chars().allMatch(Character::isWhitespace)) {
                entitySplits.add(
                        new IdentifiedEntity(
                                text,
                                lastSplit,
                                splitPoint,
                                entity.getType(),
                                entity.getPos()
                        )
                );
            }

            lastSplit = splitPoint;
        }

        if (lastSplit < entity.getEnd()) {
            entitySplits.add(
                    new IdentifiedEntity(
                            entity.getText().substring(lastSplit - entity.getStart()),
                            lastSplit,
                            entity.getEnd(),
                            entity.getType(),
                            entity.getPos()
                    )
            );
        }

        return entitySplits;
    }

    private Predicate<? super IdentifiedEntity> generateFilterWithinRange(int start, int end) {
        return identifiedEntity -> identifiedEntity.getStart() < end && identifiedEntity.getEnd() > start;
    }

    public List<IdentifiedEntity> merge(List<IdentifiedEntity> unorderedEntities) {
        List<IdentifiedEntity> toReturn = new ArrayList<>();

        List<IdentifiedEntity> identifiedEntities = sortEndStart(unorderedEntities);

        for (int i = 0; i < identifiedEntities.size();) {
            IdentifiedEntity entity = identifiedEntities.get(i);
            int j = i + 1;
            for (; j < identifiedEntities.size(); ++j) {
                IdentifiedEntity other = identifiedEntities.get(j);

                if (entity.getStart() == other.getStart() && entity.getEnd() == entity.getEnd()) {
                    entity = new IdentifiedEntity(
                            entity.getText(),
                            entity.getStart(),
                            entity.getEnd(),
                            mergeTypes(entity.getType(), other.getType()),
                            mergePos(entity.getPos(), other.getPos())
                    );
                } else {
                    break;
                }
            }

            toReturn.add(entity);
            i = j;
        }

        return toReturn;
    }

    private Set<PartOfSpeechType> mergePos(Set<PartOfSpeechType> set1, Set<PartOfSpeechType> set2) {
        Set<PartOfSpeechType> allPoS = new HashSet<>(set1);

        allPoS.addAll(set2);

        if (allPoS.size() > 1) {
            allPoS.remove(PartOfSpeechType.UNKNOWN);
        }

        return allPoS;
    }

    private Set<IdentifiedEntityType> mergeTypes(Set<IdentifiedEntityType> set1, Set<IdentifiedEntityType> set2) {
        Set<IdentifiedEntityType> allTypes = new HashSet<>(set1);

        allTypes.addAll(set2);

        return allTypes;
    }

    private List<IdentifiedEntity> mergeEntityListsAndOverlappingEntities(List<IdentifiedEntity> entityList) {
        if (entityList.isEmpty()) return entityList;

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));

        int j = 0;
        IdentifiedEntity t1 = entityList.get(j);
        for (int i = 1; i < entityList.size(); ) {
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
        for (int i = 1; i < entityList.size(); ) {
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
        for (int i = 1; i < entityList.size(); ) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getStart() <= t2.getStart() && t1.getEnd() >= t2.getEnd() && sameType(t1, t2)) {
                // merge
                final Set<IdentifiedEntityType> types = t1.getType();

                IdentifiedEntity newT1;

                if (t1.getEnd() < t2.getEnd()) {
                    newT1 = new IdentifiedEntity(t2.getText(), t1.getStart(), t2.getEnd(), types, t1.getPos());
                } else {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, t1.getPos());
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

        return entityList;
    }

    private boolean sameType(IdentifiedEntity entity1, IdentifiedEntity entity2) {
        Set<IdentifiedEntityType> types1 = entity1.getType();
        Set<IdentifiedEntityType> types2 = entity2.getType();

        return types1.containsAll(types2) && types2.containsAll(types1);
    }

    private IdentifiedEntity removeUnreliableSources(IdentifiedEntity entity) {
        return new IdentifiedEntity(
                entity.getText(),
                entity.getStart(),
                entity.getEnd(),
                entity.getType().stream().filter(this::isReliable).collect(Collectors.toSet()),
                entity.getPos()
        );
    }

    private boolean isContained(Set<IdentifiedEntityType> types, String providerType) {
        for(IdentifiedEntityType type: types) {
            if (type.getType().equals(providerType)) {
                return true;
            }
        }

        return false;
    }

    private List<IdentifiedEntity> mergeConnectedEntities(List<IdentifiedEntity> identifiedEntities, final String text) {
        identifiedEntities.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        for(ConnectedEntities connectedEntity: this.connectedEntities) {
            List<IdentifiedEntity> mergedIdentifiedEntities = new ArrayList<>();

            for (int i = 0; i < identifiedEntities.size(); ) {
                final IdentifiedEntity first = identifiedEntities.get(i);

                if (isContained(first.getType(), connectedEntity.getFirst()) && i != (identifiedEntities.size() - 1)) {
                    final IdentifiedEntity second = identifiedEntities.get(i + 1);

                    if (isContained(second.getType(), connectedEntity.getSecond())) {
                        final String connection = text.substring(first.getEnd(), second.getStart());

                        if (connectedEntity.getParticles().contains(connection.trim().toLowerCase())) {
                            // match!
                            IdentifiedEntityType identifiedEntityType =
                                    new IdentifiedEntityType(connectedEntity.getEndType(), connectedEntity.getEndSubtype(), first.getType().iterator().next().getSource());

                            IdentifiedEntity newConnectedEntity =
                                    new IdentifiedEntity(first.getText() + connection + second.getText(), first.getStart(),
                                            second.getEnd(), Collections.singleton(identifiedEntityType), first.getPos());

                            mergedIdentifiedEntities.add(newConnectedEntity);
                            i += 2;

                            continue;
                        }
                    }
                }

                i += 1;
                mergedIdentifiedEntities.add(first);
            }

            identifiedEntities = mergedIdentifiedEntities;
        }

        return identifiedEntities;
    }

    private List<IdentifiedEntity> identifyMissedRepetitions(List<IdentifiedEntity> identifiedEntities, String text, boolean missingOnly) {
        identifiedEntities.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        List<IdentifiedEntity> missedRepetitions = (missingOnly) ? new ArrayList<>() : new ArrayList<>(identifiedEntities);

        for (final IdentifiedEntity identifiedEntity : identifiedEntities) {
            final String identifiedText = identifiedEntity.getText();

            final String regExp = "\\b" + Pattern.quote(identifiedText) + "\\b";
            final Pattern pattern = Pattern.compile(regExp);
            final Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                if (! listContains(missedRepetitions, start, end)) {
                    missedRepetitions.add(new IdentifiedEntity(identifiedText, start, end, identifiedEntity.getType(), Collections.singleton(PartOfSpeechType.UNKNOWN)));
                }
            }
        }

        return missedRepetitions;
    }

    private boolean listContains(final List<IdentifiedEntity> identifiedEntityList, final int start, final int end) {
        for (final IdentifiedEntity identifiedEntity : identifiedEntityList) {
            if (identifiedEntity.getStart() == start && identifiedEntity.getEnd() == end) return true;
        }

        return false;
    }

    private boolean checkBlacklist(IdentifiedEntity identifiedEntity) {
        final Set<String> defaults = this.blacklistedValues.getOrDefault("*", Collections.emptySet());
        final List<Identifier> defaultIdentifiers = this.blacklistIdentifierBased.getOrDefault("*", Collections.emptyList());
        
        for (IdentifiedEntityType providerTypeEntity: identifiedEntity.getType()) {
            String providerType = providerTypeEntity.getType();

            Set<String> values = this.blacklistedValues.getOrDefault(providerType, defaults);
            String text = identifiedEntity.getText().toLowerCase();

            if (values.contains(text)) {
                return false;
            }

            List<Identifier> identifierList = this.blacklistIdentifierBased.getOrDefault(providerType, defaultIdentifiers);
            
            for(Identifier identifier: identifierList) {
                if (identifier.isOfThisType(identifiedEntity.getText())) {
                    return false;
                }
            }
        }

        return true;
    }

    private IdentifiedEntity resolveType(IdentifiedEntity entity) {
        final IdentifiedEntityType bestType = extractBestType(entity);

        if (null == bestType || bestType.getType().isEmpty()) return null;

        return new IdentifiedEntity(entity.getText(), entity.getStart(), entity.getEnd(), Collections.singleton(bestType), entity.getPos());
    }

    private IdentifiedEntityType extractBestType(IdentifiedEntity entity) {
        Set<IdentifiedEntityType> types = entity.getType();

        final Map<IdentifiedEntityType, Double> counters = new HashMap<>(types.size());

        for (final IdentifiedEntityType typeEntity : types) {
            double weight = getEntityTypeWeight(typeEntity);

            if (counters.containsKey(typeEntity)) {
                counters.put(typeEntity, counters.get(typeEntity) + weight);
            } else {
                counters.put(typeEntity, weight);
            }
        }

        if (counters.isEmpty()) return null;

        if (1 == counters.size()) {
            return counters.keySet().iterator().next();
        } else {
            return  new ArrayList<>(counters.entrySet()).stream().reduce((a, b) -> {
                if (a.getValue() > b.getValue()) {
                    return a;
                } else if (Objects.equals(a.getValue(), b.getValue())) {
                    if ("NATIONAL_ID".equals(a.getKey().getType())) {
                        return a;
                    }
                }
                return b;
            }).orElseThrow(() -> new RuntimeException("Ugly, should not happen")).getKey();
        }
    }

    private double getEntityTypeWeight(IdentifiedEntityType typeEntity) {
        return weights.getOrDefault(typeEntity.getSource(), Collections.emptyMap())
                .getOrDefault(typeEntity.getType(), 1.0);
    }

    private boolean isReliable(IdentifiedEntityType type) {
        return !unreliableSource.getOrDefault(type.getSource(), Collections.emptySet())
                .contains(type.getType());
    }

    private boolean filterIncorrectIdentifications(IdentifiedEntity identifiedEntity) {
        return !Objects.isNull(identifiedEntity) &&
                !identifiedEntity.getType().isEmpty() &&
                preservePOSIndependentAndNounLike(identifiedEntity) &&
                !removeNumericOnly(identifiedEntity);
    }

    private boolean removeNumericOnly(IdentifiedEntity identifiedEntity) {
        Set<IdentifiedEntityType> types = identifiedEntity.getType();

        return types.size() == 1 && types.iterator().next().getType().equals("NUMERIC");
    }

    private boolean isPOSIndependent(String type, String value) {
        return posIndependent.containsKey(type) && ! posIndependent.get(type).contains(value.toLowerCase().trim());
    }

    private boolean isPOSIndependent(IdentifiedEntity identifiedEntity) {
        for(IdentifiedEntityType typeEntity: identifiedEntity.getType()) {
            String type = typeEntity.getType();

            if (isPOSIndependent(type, identifiedEntity.getText())) {
                return true;
            }
        }

        return false;
    }

    private boolean preservePOSIndependentAndNounLike(IdentifiedEntity identifiedEntity) {
        if (isPOSIndependent(identifiedEntity)) {
            return true;
        }

        boolean hasNounOrNounLike = identifiedEntity.getPos().stream().anyMatch(pos -> pos.isNoun() || pos.isAdjective());

        boolean hasVerbOrModalOrLike = identifiedEntity.getPos().stream().anyMatch(pos -> pos.isModal() || pos.isVerb() || pos.isAdverb() || pos.isPronoun());
        boolean onlyConjunction = !identifiedEntity.getPos().isEmpty() && identifiedEntity.getPos().stream().allMatch(PartOfSpeechType::isConjunction);
        boolean onlyPunctuation = !identifiedEntity.getPos().isEmpty() && identifiedEntity.getPos().stream().allMatch(PartOfSpeechType::isPuntuation);

        return (hasNounOrNounLike || !hasVerbOrModalOrLike) && !onlyConjunction && !onlyPunctuation;
    }

    @Override
    public String getName() {
        return "ComplexFreeTextAnnotator" + nlpAnnotators.stream().map(NLPAnnotator::getName).collect(Collectors.toList()).toString();
    }

    private Map<String, Set<String>> buildBlacklist(JsonNode configuration) {
        final Map<String, Set<String>> map = new HashMap<>();

        configuration.fields().forEachRemaining( entry -> {
            final String providerType = entry.getKey();
            final Set<String> vals = new HashSet<>();

            entry.getValue().elements().forEachRemaining( element -> {
                String term = element.asText();

                if (term.startsWith("{{") && term.endsWith("}}")) {
                    return;
                } else {
                    vals.add(term.toLowerCase());
                }
            });

            map.put(providerType, vals);
        } );

        return map;
    }

    @SuppressWarnings("unchecked")
    private Identifier createInstance(String fullyQualifiedClassName) {
        try {
            Class<? extends Identifier> identifierClassObject = (Class<? extends Identifier>) Class.forName(fullyQualifiedClassName);
            Constructor<? extends Identifier> constructor = identifierClassObject.getConstructor();

            return constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate class of " + fullyQualifiedClassName, e);
        }

    }

    private Map<String, Set<String>> extractPostIndependent(JsonNode configuration) {
        final Map<String, Set<String>> posIndependent = new HashMap<>();

        configuration.elements().forEachRemaining( element -> {
            if (element.isObject()) {
                element.fields().forEachRemaining( field -> {
                    String name = field.getKey();
                    Set<String> exceptions = new HashSet<>();

                    field.getValue().elements().forEachRemaining( e -> exceptions.add(e.asText().toLowerCase()));

                    posIndependent.put(name, exceptions);
                });
            } else {
                posIndependent.put(element.asText(), Collections.emptySet());
            }
        });

        return posIndependent;
    }

    public void addNonPosDependentType(String type) {
        if (!posIndependent.containsKey(type)) {
            posIndependent.put(type, Collections.emptySet());
        }
    }

    protected List<NLPAnnotator> getNLPAnnotators() {
        return this.nlpAnnotators;
    }
}