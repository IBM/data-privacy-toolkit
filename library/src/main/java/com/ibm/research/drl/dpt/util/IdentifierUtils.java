/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.CharacterRequirements;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class IdentifierUtils {
    private static final Logger log = LogManager.getLogger(IdentifierUtils.class);

    public static boolean looksLikeFreeText(String value) {
        if (value.length() < 10) {
            return false;
        }

        int whitespaceRegions = 0;

        for (int i = 0; i < value.length() && whitespaceRegions < 2; i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                whitespaceRegions++;
                while (i < value.length() && Character.isWhitespace(value.charAt(i))) {
                    i++;
                }
            }
        }

        return whitespaceRegions >= 2;
    }

    public static int createCharacterProfile(String input) {
        int mask = CharacterRequirements.NONE;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                mask |= CharacterRequirements.DIGIT;
            } else if (Character.isAlphabetic(c)) {
                mask |= CharacterRequirements.ALPHA;
            } else if (Character.isWhitespace(c)) {
                mask |= CharacterRequirements.SPACE;
            } else if (c == '.') {
                mask |= CharacterRequirements.DOT;
            } else if (c == '@') {
                mask |= CharacterRequirements.AT;
            } else if (c == '-') {
                mask |= CharacterRequirements.DASH;
            } else if (c == ':') {
                mask |= CharacterRequirements.COLUMN;
            }
        }

        return mask;
    }

    public static int fillCharacterMap(String input, int[] counters) {
        int nonASCII = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c > 256) {
                nonASCII++;
                continue;
            }

            counters[c]++;
        }

        return nonASCII;
    }

    private static int calculateFrequency(long count, long numberOfIdentified, long numberOfEmpty,
                                          long numberOfUnknown, boolean considerEmptyForConfidence) {
        long totalCount = numberOfIdentified;
        if (considerEmptyForConfidence) {
            totalCount += numberOfEmpty;
        }

        if (totalCount == 0) {
            return -1;
        }

        return (int) (100 * count / totalCount);
    }

    private static boolean typeIdentifiedLessThanRequiredFrequency(String typeName, int typeConfidence, IdentificationConfiguration identificationConfiguration) {
        return (typeConfidence < identificationConfiguration.getFrequencyThresholdForType(typeName));
    }

    public static IdentifiedType findBestType(Collection<IdentifiedType> identifiedTypes, Long rowsProcessed, IdentificationConfiguration identificationConfiguration) {
        switch (identificationConfiguration.getIdentificationStrategy()) {
            case FREQUENCY_BASED:
                return findBestTypeFrequencyBased(identifiedTypes, rowsProcessed, identificationConfiguration);
            case PRIORITY_BASED:
                return findBestTypePriorityBased(identifiedTypes, rowsProcessed, identificationConfiguration);
            default:
                throw new RuntimeException("Not implemented yet");
        }

    }

    private static IdentifiedType findBestTypePriorityBased(Collection<IdentifiedType> identifiedTypes, Long rowsProcessed,
                                                            IdentificationConfiguration identificationConfiguration) {
        IdentifiedType bestType = new IdentifiedType(ProviderType.UNKNOWN.name(), -1);
        int bestTypePriority = -1;
        final boolean considerEmptyForConfidence = identificationConfiguration.getConsiderEmptyForFrequency();

        long numberOfEmpty = 0;
        long numberOfUnknown = 0;

        for (IdentifiedType type : identifiedTypes) {
            if (type.getTypeName().equals(ProviderType.EMPTY.name())) {
                numberOfEmpty += type.getCount();
            } else if (type.getTypeName().equals(ProviderType.UNKNOWN.name())) {
                numberOfUnknown += type.getCount();
            }
        }

        long numberOfIdentified = rowsProcessed - numberOfEmpty;

        for (IdentifiedType type : identifiedTypes) {
            String typeName = type.getTypeName();
            if (typeName.equals(ProviderType.EMPTY.name()) || typeName.equals(ProviderType.UNKNOWN.name())) {
                continue;
            }

            int typeFrequency = calculateFrequency(type.getCount(), numberOfIdentified, numberOfEmpty,
                    numberOfUnknown, considerEmptyForConfidence);

            if (typeIdentifiedLessThanRequiredFrequency(typeName, typeFrequency, identificationConfiguration)) {
                continue;
            }

            int typePriority = identificationConfiguration.getPriorityForType(typeName);

            if (typePriority > bestTypePriority || typePriority == bestTypePriority && type.getCount() > bestType.getCount()) {
                bestType = type;
                bestTypePriority = typePriority;
            }
        }

        return bestType;
    }


    // 100 values
    // 30 names <-
    // 30 city
    // 65 unknown

    private static IdentifiedType findBestTypeFrequencyBased(Collection<IdentifiedType> identifiedTypes, Long rowsProcessed, IdentificationConfiguration identificationConfiguration) {
        IdentifiedType bestType = new IdentifiedType(ProviderType.UNKNOWN.name(), -1);
        int bestTypeFrequency = -1;
        final boolean considerEmptyForConfidence = identificationConfiguration.getConsiderEmptyForFrequency();

        long numberOfEmpty = 0;
        long numberOfUnknown = 0;

        for (IdentifiedType type : identifiedTypes) {
            if (type.getTypeName().equals(ProviderType.EMPTY.name())) {
                numberOfEmpty += type.getCount();
            } else if (type.getTypeName().equals(ProviderType.UNKNOWN.name())) {
                numberOfUnknown += type.getCount();
            }
        }

        long numberOfIdentified = rowsProcessed - numberOfEmpty;

        log.debug("empty: {} u: {} i: {}", numberOfEmpty, numberOfUnknown, numberOfIdentified);
        for (IdentifiedType type : identifiedTypes) {
            String typeName = type.getTypeName();
            if (typeName.equals(ProviderType.EMPTY.name()) || typeName.equals(ProviderType.UNKNOWN.name())) {
                continue;
            }

            log.debug("type: {} count: {}", typeName, type.getCount());

            int typeFrequency = calculateFrequency(type.getCount(), numberOfIdentified, numberOfEmpty,
                    numberOfUnknown, considerEmptyForConfidence);

            log.debug("frequency: {}", typeFrequency);

            if (typeIdentifiedLessThanRequiredFrequency(typeName, typeFrequency, identificationConfiguration)) {
                continue;
            }

            log.debug("frequency threshold exceeded");
            if (bestType == null) {
                bestType = type;
                bestTypeFrequency = typeFrequency;
                continue;
            }

            if (typeFrequency > bestTypeFrequency) {
                bestType = type;
                bestTypeFrequency = typeFrequency;
            } else if (typeFrequency == bestTypeFrequency) {
                if (identificationConfiguration.getPriorityForType(typeName) > identificationConfiguration.getPriorityForType(bestType.getTypeName())) {
                    bestType = type;
                    bestTypeFrequency = typeFrequency;
                }
            }
        }

        return bestType;
    }

    public static Map<String, IdentifiedType> getIdentifiedType(Map<String, List<IdentifiedType>> values, long rowsProcessed, IdentificationConfiguration identificationConfiguration) {
        Map<String, IdentifiedType> identifiedTypes = new HashMap<>(values.size());

        for (String fieldName : values.keySet()) {
            IdentifiedType bestType = findBestType(values.get(fieldName), rowsProcessed, identificationConfiguration);

            if (null != bestType) {
                identifiedTypes.put(fieldName, bestType);
            }
        }

        return identifiedTypes;
    }

    public static Map<ProviderType, Long> identifySingleValue(String value) {
        Map<ProviderType, Long> results = new HashMap<>();

        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();

        for (Identifier identifier : identifiers) {
            if (identifier.isOfThisType(value)) {
                ProviderType providerType = identifier.getType();
                Long counter = results.get(providerType);

                if (counter == null) {
                    counter = 1L;
                } else {
                    counter += 1L;
                }

                results.put(providerType, counter);
            }
        }

        return results;
    }

    public static Map<String, List<IdentifiedType>> organizeToCollection(Map<String, Map<String, Counter>> allTypes) {
        Map<String, List<IdentifiedType>> results = new HashMap<>();
        for (Map.Entry<String, Map<String, Counter>> entry : allTypes.entrySet()) {
            String columnName = entry.getKey();

            Map<String, Counter> counters = entry.getValue();

            List<IdentifiedType> identifiedTypes = new ArrayList<>();

            for (Map.Entry<String, Counter> counterEntry : counters.entrySet()) {
                identifiedTypes.add(new IdentifiedType(counterEntry.getKey(), counterEntry.getValue().counter));
            }

            results.put(columnName, identifiedTypes);
        }

        return results;
    }
}
