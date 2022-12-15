/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;
import java.util.*;

public class ReplaceMaskingProvider extends AbstractMaskingProvider {
    private final int preservedCharacters;
    private final int offset;
    private final boolean replaceOnValueInSet;
    private final Set<String> testValues;
    private final boolean replaceOnValueNotInSet;
    private final List<String> replacementValues;
    private final ReplaceMode replaceMode;
    private final String asterisk;

    private final String prefix;

    public enum ReplaceMode {
        WITH_PARTIAL,
        WITH_RANDOM,
        WITH_ASTERISKS,
        WITH_DETERMINISTIC,
        WITH_SET
    }

    /**
     * Instantiates a new Truncate masking provider.
     */
    public ReplaceMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Replace masking provider.
     *
     * @param random the random
     */
    public ReplaceMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Replace masking provider.
     *
     * @param random        the random
     * @param configuration the masking configuration
     */
    public ReplaceMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.offset = configuration.getIntValue("replace.mask.offset");
        this.preservedCharacters = configuration.getIntValue("replace.mask.preserve");
        String star = configuration.getStringValue("replace.mask.asteriskValue");
        this.asterisk = Objects.isNull(star) ? "*" : star;
        String mode = configuration.getStringValue("replace.mask.mode");
        this.replaceMode = ReplaceMode.valueOf(Objects.isNull(mode) ? "WITH_PARTIAL" : configuration.getStringValue("replace.mask.mode"));
        this.replaceOnValueInSet = configuration.getBooleanValue("replace.mask.replaceOnValueInSet");
        this.replaceOnValueNotInSet = configuration.getBooleanValue("replace.mask.replaceOnValueNotInSet");
        this.testValues = new HashSet<>();
        this.replacementValues = new ArrayList<>();

        this.prefix = configuration.getStringValue("replace.mask.prefix");

        JsonNode replacementValuesNode = configuration.getJsonNodeValue("replace.mask.replacementValueSet");

        if (null != replacementValuesNode) {
            replacementValuesNode.elements().forEachRemaining(node -> {
                this.replacementValues.add(node.asText());
            });
        }

        // "000-0000-0000-000-00" -> <prefix>-[UNIQUE]

        if (this.replaceOnValueInSet || this.replaceOnValueNotInSet) {
            configuration.getJsonNodeValue("replace.mask.testValues").elements().forEachRemaining(node -> {
                testValues.add(node.asText());
            });
        }

        /*

        123-123-123 -> 123-XXX-XXX

        123-abc-123 -> XXX-abc-XXX

        this.optionMap.put("replace.mask.offset", new ConfigurationOption(0, "Starting offset for preserving", "Replace"));
        this.optionMap.put("replace.mask.replaceWithAsterisks", new ConfigurationOption(false, "Replace the rest of the value with asterisks", "Replace"));
        this.optionMap.put("replace.mask.replaceWithRandom", new ConfigurationOption(false, "Replace the rest of the value with random digits/characters", "Replace"));
        this.optionMap.put("replace.mask.preserve", new ConfigurationOption(3, "Number of characters to preserve", "Replace"));
        this.optionMap.put("replace.mask.replaceOnValueInSet", new ConfigurationOption(false, "Replace only if the value is in a set", "Replace"));
        this.optionMap.put("replace.mask.replaceOnValueNotInSet", new ConfigurationOption(false, "Replace only if the value is not in a set", "Replace"));
        this.optionMap.put("replace.mask.replacementValueSet", new ConfigurationOption(null, "Set of values to be used for replacement", "Replace"));
        this.optionMap.put("replace.mask.replaceFromSet", new ConfigurationOption(false, "Replace from a fixed set of values", "Replace"));
        this.optionMap.put("replace.mask.testValues",



        
         */
    }

    /**
     * Instantiates a new Replace masking provider.
     *
     * @param configuration the configuration
     */
    public ReplaceMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    @Override
    public String mask(String identifier) {
        return mask(identifier, true);
    }

    private String mask(String identifier, boolean checkSetConstrains) {
        if (checkSetConstrains && shouldValueBePreserved(identifier)) {
            return identifier;
        }

        if (this.replaceMode == ReplaceMode.WITH_SET) {
            int position = random.nextInt(replacementValues.size());

            return replacementValues.get(position);
        }

        int identifierLength = identifier.length();
        if (offset > identifierLength) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        if (prefix != null) {
            builder.append(prefix);
        }

        int stop = offset + preservedCharacters;
        if (stop > identifierLength) {
            stop = identifierLength;
        }

        String maskedValue = identifier.substring(this.offset, stop);
        if (this.replaceMode == ReplaceMode.WITH_PARTIAL) {
            builder.append(maskedValue);
            return builder.toString();
        }

        if (this.offset > 0) {
            if (this.replaceMode == ReplaceMode.WITH_ASTERISKS) {
                for (int i = 0; i < this.offset; i++) {
                    builder.append(asterisk);
                }
            } else {
                if (this.replaceMode == ReplaceMode.WITH_RANDOM) {
                    builder.append(RandomGenerators.randomReplacement(identifier.substring(0, this.offset)));
                } else if (this.replaceMode == ReplaceMode.WITH_DETERMINISTIC) {
                    builder.append(RandomGenerators.deterministicReplacement(identifier.substring(0, this.offset)));
                }
            }
        }

        builder.append(maskedValue);

        if (stop < identifierLength) {
            if (this.replaceMode == ReplaceMode.WITH_ASTERISKS) {
                for (int i = stop; i < identifierLength; i++) {
                    builder.append(asterisk);
                }
            } else if (this.replaceMode == ReplaceMode.WITH_RANDOM) {
                builder.append(RandomGenerators.randomReplacement(identifier.substring(stop, identifierLength)));
            } else if (this.replaceMode == ReplaceMode.WITH_DETERMINISTIC) {
                builder.append(RandomGenerators.deterministicReplacement(identifier.substring(stop, identifierLength)));
            }
        }

        return builder.toString();
    }

    @Override
    public String mask(String identifier, String fieldName,
                       FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> maskedValues) {
        if (fieldRelationship.getRelationshipType() == RelationshipType.KEY) {
            String operandFieldName = fieldRelationship.getOperands()[0].getName();
            OriginalMaskedValuePair originalMaskedValuePair = maskedValues.get(operandFieldName);
            String operandValue = originalMaskedValuePair.getOriginal();

            if (shouldValueBePreserved(operandValue)) {
                return identifier;
            }
            return mask(identifier, false);
        }
        return mask(identifier);
    }

    private boolean shouldValueBePreserved(String value) {
        if (replaceOnValueInSet && !testValues.contains(value)) {
            return true;
        }

        return replaceOnValueNotInSet && testValues.contains(value);
    }
}
