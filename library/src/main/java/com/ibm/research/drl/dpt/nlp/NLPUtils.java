/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.nlp;


import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.MedicalPatternIdentifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for NLP-related operations: annotation, masking, and text transformation.
 */
public class NLPUtils {
    private static class CacheEntry extends HashMap<String, String> {}
    private static class Cache extends HashMap<String, CacheEntry> {}

    /** Not instantiable. */
    private NLPUtils() {}
    
    private static final Cache cache = new Cache();

    /**
     * Creates a space-joined string of inline-XML annotations for the given entity tokens.
     *
     * @param annotatedTokens the list of annotated tokens
     * @return the joined annotation string
     */
    public static String createString(List<IdentifiedEntity> annotatedTokens) {
        List<String> tokens = new ArrayList<>();

        for(IdentifiedEntity identifiedEntity: annotatedTokens) {
            tokens.add(identifiedEntity.toInlineXML());
        }

        return StringUtils.join(tokens, " ");
    }

    private static String extractNumber(String toMask) {
        for (Pattern pattern : MedicalPatternIdentifier.patterns) {
            Matcher matcher = pattern.matcher(toMask);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        throw new RuntimeException("Unable to properly extract MRN id");
    }

    /** Function that converts an entity to its inline-XML form. */
    public static final Function<IdentifiedEntity, String> ANNOTATE_FUNCTION = IdentifiedEntity::toInlineXML;

    /** Function that returns the original text of an entity. */
    public static final Function<IdentifiedEntity, String> IDENTITY_FUNCTION = IdentifiedEntity::getText;

    /** Function that redacts the entity text (replaces each character with {@code *}). */
    public static final Function<IdentifiedEntity, String> REDACT_FUNCTION = (entity) -> {

        final StringBuilder builder = new StringBuilder();
        String toMask = entity.getText();
        if ("MRN".equals(entity.getType().iterator().next().getType())) {
            try {
                final String id = extractNumber(toMask);
                final int indexOfId = toMask.indexOf(id);
                final String prehamble = toMask.substring(0, indexOfId);
                builder.append(prehamble);
                toMask = id;
            } catch (Exception ignored) {
            }
        }

        builder.append("*".repeat(toMask.length()));

        return builder.toString();
    };

    /** Function that replaces entity text with a type-based tag, caching the assignment. */
    public static final Function<IdentifiedEntity, String> TAG_FUNCTION_WITH_CACHE = (entity) -> {
        final String type = entity.getType().iterator().next().getType();

        final CacheEntry typeCache = cache.computeIfAbsent(type, k -> new CacheEntry());

        String toTag = entity.getText();
        String preamble = "";
        if ("MRN".equals(entity.getType().iterator().next().getType())) {
            try {
                final String id = extractNumber(entity.getText());
                final int indexOfId = toTag.indexOf(id);
                preamble = toTag.substring(0, indexOfId);
                toTag = id;
            } catch (Exception ignore) {
            }
        }

        return preamble + typeCache.computeIfAbsent(toTag, k -> type + "-" + typeCache.size());
    };
    
    /**
     * Applies the given function to each identified entity in the text, replacing its span.
     *
     * @param inputText           the original text
     * @param identifiedEntities  the list of identified entities
     * @param function            the function to apply to each entity
     * @return the transformed text
     */
    public static String applyFunction(String inputText,
                                       List<IdentifiedEntity> identifiedEntities,
                                       Function<IdentifiedEntity, String> function) {
        List<IdentifiedEntity> sorted = identifiedEntities.stream()
                .sorted(Comparator.comparingInt(IdentifiedEntity::getEnd).thenComparingInt(IdentifiedEntity::getStart))
                .toList();
        identifiedEntities = sorted;

        StringBuilder builder = new StringBuilder();
        int lastOffset = 0;

        for (IdentifiedEntity entity: identifiedEntities) {
            builder.append(inputText, lastOffset, entity.getStart());

            builder.append(function.apply(entity));

            lastOffset = entity.getEnd();
        }

        builder.append(inputText.substring(lastOffset));

        return builder.toString();
    }
    
    /**
     * Masks identified entities in the given text using the masking provider factory.
     *
     * @param text                    the original text
     * @param identifiedEntities      the entities to mask
     * @param maskingProviderFactory  the masking provider factory
     * @return the masked text
     */
    public static String maskAnnotatedText(final String text, final List<IdentifiedEntity> identifiedEntities, final MaskingProviderFactory maskingProviderFactory) {
        String maskedText = text;

        List<IdentifiedEntity> sorted = identifiedEntities.stream()
                .sorted(Comparator.comparingInt(IdentifiedEntity::getStart).reversed())
                .toList();
        for (final IdentifiedEntity identifiedEntity : sorted) {
            maskedText = maskTextForIdentifiedEntity(text, identifiedEntity, maskingProviderFactory);
        }

        return maskedText;
    }

    private static String maskTextForIdentifiedEntity(final String text, final IdentifiedEntity identifiedEntity, final MaskingProviderFactory maskingProviderFactory) {
        final String identifiedType = identifiedEntity.getType().iterator().next().getType();

        final ProviderType providerType = ProviderType.valueOf(identifiedType);
        final MaskingConfiguration maskingConfiguration = maskingProviderFactory.getConfigurationForField(identifiedType);
        final MaskingProvider maskingProvider = maskingProviderFactory.get(providerType, maskingConfiguration);

        final String maskedText = maskingProvider.mask(identifiedEntity.getText());

        return text.substring(0, identifiedEntity.getStart()) + maskedText + text.substring(identifiedEntity.getEnd());
    }

    /**
     * Masks each annotated token in the list using the appropriate masking provider.
     *
     * @param annotatedTokens        the list of annotated tokens
     * @param maskingProviderFactory the masking provider factory
     * @return the list of masked tokens
     */
    public static List<IdentifiedEntity> maskAnnotatedTokens(List<IdentifiedEntity> annotatedTokens, MaskingProviderFactory maskingProviderFactory) {

        List<IdentifiedEntity> maskedTokens = new ArrayList<>();

        for(IdentifiedEntity annotatedToken: annotatedTokens) {
            Set<IdentifiedEntityType> types = annotatedToken.getType();
            
            if (types == null || types.isEmpty()) {
                maskedTokens.add(annotatedToken);
            } else {
                ProviderType providerType = ProviderType.valueOf(types.iterator().next().getType());
                String fieldName = providerType.name();
                MaskingProvider maskingProvider = maskingProviderFactory.get(fieldName, providerType);
                maskedTokens.add(new IdentifiedEntity(maskingProvider.mask(annotatedToken.getText()), annotatedToken.getStart(), annotatedToken.getEnd(), 
                        types, Collections.singleton(PartOfSpeechType.UNKNOWN)));
            }
        }

        return maskedTokens;
    }

    /**
     * Masks tagged entities in annotated text (which uses {@code <ProviderType:...>} tags).
     *
     * @param originalText           the tagged text
     * @param keepTags               whether to keep the XML tags in the output
     * @param maskingProviderFactory the masking provider factory
     * @return the masked text
     */
    public static String maskAnnotatedText(String originalText, boolean keepTags, MaskingProviderFactory maskingProviderFactory) {
        String pattern = "<ProviderType:";
        String closingPattern = "</ProviderType>";

        int fromIndex = 0;

        StringBuilder builder = new StringBuilder();

        int index = originalText.indexOf(pattern, fromIndex);
        if (index == -1) {
            return originalText;
        }

        while (index != -1) {
            builder.append(originalText, fromIndex, index);

            int closingBracket = originalText.indexOf('>', index);
            if (closingBracket == -1) {
                builder.append(originalText.substring(index));
                break;
            }

            String providerTypeName = originalText.substring(index + pattern.length(), closingBracket);

            int closingPatternIndex = originalText.indexOf(closingPattern, closingBracket);
            if (closingPatternIndex == -1) {
                builder.append(originalText.substring(index));
                break;
            }

            String value = originalText.substring(closingBracket + 1, closingPatternIndex);

            MaskingProvider maskingProvider = maskingProviderFactory.get(providerTypeName, ProviderType.valueOf(providerTypeName));

            if (keepTags) {
                builder.append(pattern).append(providerTypeName).append('>');
            }

            builder.append(maskingProvider.mask(value));

            if (keepTags) {
                builder.append(closingPattern);
            }

            fromIndex = closingPatternIndex + closingPattern.length();
            index = originalText.indexOf(pattern, fromIndex);

            if (index == -1) {
                builder.append(originalText.substring(fromIndex));
            }
        }

        return builder.toString();
    }
}
