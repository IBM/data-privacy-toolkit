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
package com.ibm.research.drl.dpt.nlp.masking;

import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntityType;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.nlp.NLPUtils;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FreeTextMaskingProvider extends AbstractComplexMaskingProvider<String> {
    private final static Logger logger = LogManager.getLogger(FreeTextMaskingProvider.class);
    private final ComplexFreeTextAnnotator annotator;
    private final String lookupTokensSeparator;
    private final boolean lookupTokensIgnoreCase;
    private final boolean lookupTokensFindAnywhere;
    private final String lookupTokensType;

    public FreeTextMaskingProvider(MaskingProviderFactory factory, ComplexFreeTextAnnotator annotator) {
        super("freetext", factory.getConfigurationForField(""), Collections.emptySet(), factory);
        this.annotator = annotator;

        this.lookupTokensSeparator = factory.getConfigurationForField("").getStringValue("generic.lookupTokensSeparator");
        this.lookupTokensIgnoreCase = factory.getConfigurationForField("").getBooleanValue("generic.lookupTokensIgnoreCase");
        this.lookupTokensFindAnywhere = factory.getConfigurationForField("").getBooleanValue("generic.lookupTokensFindAnywhere");
        this.lookupTokensType = factory.getConfigurationForField("").getStringValue("generic.lookupTokensType");

        logger.info("Initialization of FreeTextMaskingProvider completed");
    }

    public FreeTextMaskingProvider(MaskingProviderFactory factory, MaskingConfiguration configuration) {
        this(factory, buildNLPAnnotator(configuration));
    }

    private static ComplexFreeTextAnnotator buildNLPAnnotator(MaskingConfiguration configuration) {
        return  new ComplexFreeTextAnnotator(configuration.getJsonNodeValue("freetext.mask.nlp.config"));
    }

    @Override
    public String mask(String text)  {
        try {
            final List<IdentifiedEntity> identifiedEntities = identifyEntities(text);
            final List<IdentifiedEntity> maskedEntities = maskIdentifiedEntities(identifiedEntities);

            return NLPUtils.applyFunction(text, maskedEntities, NLPUtils.IDENTITY_FUNCTION);
        } catch (IOException e) {
            logger.error("Problem when masking text");
            throw new RuntimeException(e);
        }
    }


    @Override
    public String maskGrepAndMask(String text, List<String> targetTokens) {
        List<IdentifiedEntity> greppedEntities = new ArrayList<>();

        for(String targetText : targetTokens) {
            greppedEntities.addAll(grep(targetText, text,
                    this.lookupTokensSeparator, this.lookupTokensIgnoreCase, this.lookupTokensFindAnywhere, this.lookupTokensType));
        }

        try {
            final List<IdentifiedEntity> identifiedEntities = identifyEntities(text);
            identifiedEntities.addAll(greppedEntities);

            final List<IdentifiedEntity> maskedEntities = maskIdentifiedEntities(annotator.merge(identifiedEntities));

            return NLPUtils.applyFunction(text, maskedEntities, NLPUtils.IDENTITY_FUNCTION);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<IdentifiedEntity> maskIdentifiedEntities(List<IdentifiedEntity> identifiedEntities) {
        return identifiedEntities.stream()
                .map(
                        entity -> {
                            String type = entity.getType().iterator().next().getType();
                            if (factory.getToBeMasked().containsKey(type)) {
                                String maskedValue = factory.get(type, factory.getToBeMasked().get(type).getProviderType()).mask(entity.getText());

                                return new IdentifiedEntity(
                                        truncateOrPad(maskedValue, entity.getEnd() - entity.getStart()),
                                        entity.getStart(), entity.getEnd(), entity.getType(), entity.getPos());
                            }
                            return entity;
                        }
                )
                .collect(Collectors.toList());
    }

    private String truncateOrPad(String maskedValue, int length) {
        StringBuilder builder = new StringBuilder();

        builder.append(maskedValue, 0, Math.min(length, maskedValue.length()));

        builder.append(" ".repeat(length - builder.length()));

        return builder.toString();
    }

    private List<IdentifiedEntity> identifyEntities(String text) throws IOException {
        return annotator.identify(text, Language.UNKNOWN);
    }

    protected List<IdentifiedEntity> grep(String sourceTokens, String targetValue, String separator,
                                          boolean ignoreCase,
                                          boolean findAnywhere,
                                          String tokenType) {
        if (sourceTokens == null ||  sourceTokens.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> lookupValues = new HashSet<>();

        String[] tokens = sourceTokens.split(separator);
        Collections.addAll(lookupValues, tokens);

        if (lookupValues.isEmpty()) {
            return Collections.emptyList();
        }

        int patternFlags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;

        Set<Pattern> patterns = new HashSet<>();
        for(String lookupValue: lookupValues) {
            if (findAnywhere) {
                patterns.add(Pattern.compile(lookupValue, patternFlags));
            } else {
                patterns.add(Pattern.compile("\\b" + lookupValue + "\\b", patternFlags));
            }
        }

        List<IdentifiedEntity> results = new ArrayList<>();

        for (Pattern pattern: patterns) {
            Matcher matcher = pattern.matcher(targetValue);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                String text = targetValue.substring(start, end);

                IdentifiedEntity entity = new IdentifiedEntity(text, start, end,
                        Collections.singleton(new IdentifiedEntityType(tokenType, tokenType, "__LOOKUP_TOKENS__")),
                        Collections.singleton(PartOfSpeechType.UNKNOWN));

                results.add(entity);
            }
        }

        results.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        return results;
    }
}
