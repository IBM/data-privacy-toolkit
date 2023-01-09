/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.masking;

import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntityType;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.nlp.NLPUtils;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FreeTextMaskingProvider extends AbstractComplexMaskingProvider<String> {
    private final static Logger logger = LogManager.getLogger(FreeTextMaskingProvider.class);
    private final NLPAnnotator annotator;
    private final Map<String, DataMaskingTarget> toBeMasked;

    public FreeTextMaskingProvider(MaskingProviderFactory factory, NLPAnnotator annotator, Map<String, DataMaskingTarget> toBeMasked) {
        super("freetext", factory.getConfigurationForField(""), Collections.emptySet(), factory);
        this.annotator = annotator;
        this.toBeMasked = toBeMasked;

        logger.info("Initialization of FreeTextMaskingProvider completed");
    }

    public FreeTextMaskingProvider(MaskingProviderFactory factory, MaskingConfiguration configuration, Map<String, DataMaskingTarget> toBeMasked) {
        this(factory, buildNLPAnnotator(configuration), toBeMasked);
    }

    private static NLPAnnotator buildNLPAnnotator(MaskingConfiguration configuration) {
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

    private List<IdentifiedEntity> maskIdentifiedEntities(List<IdentifiedEntity> identifiedEntities) {
        return identifiedEntities.stream()
                .map(
                        entity -> {
                            String type = entity.getType().iterator().next().getType();
                            if (toBeMasked.containsKey(type)) {
                                String maskedValue = factory.get(type, toBeMasked.get(type).getProviderType()).mask(entity.getText());

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
