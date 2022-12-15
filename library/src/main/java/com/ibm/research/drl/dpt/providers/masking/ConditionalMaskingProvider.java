/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class ConditionalMaskingProvider extends AbstractMaskingProvider {
    private final MaskingProvider maskingProvider;
    private final boolean isWhiteListEnabled;

    private final Collection<Pattern> patterns;

    public ConditionalMaskingProvider(MaskingConfiguration configuration, MaskingProviderFactory maskingProviderFactory) {
        isWhiteListEnabled = configuration.getBooleanValue("conditional.isWhitelist");
        patterns = buildPatterns(configuration.getJsonNodeValue("conditional.patterns"));
        maskingProvider = maskingProviderFactory.get(
                configuration.getStringValue("conditional.fieldName"),
                ProviderType.valueOf(configuration.getStringValue("conditional.providerName"))
        );
    }

    private Collection<Pattern> buildPatterns(JsonNode patternConfigurations) {
        List<Pattern> patterns = new ArrayList<>();

        patternConfigurations.elements().forEachRemaining(
                configuration -> {
                    String regex = extactRegularExpression(configuration);
                    int flags = buildFlags(configuration);

                    patterns.add(
                            Pattern.compile(regex, flags)
                    );
                });

        return patterns;
    }

    private int buildFlags(JsonNode configuration) {
        if (configuration.isObject()) {
            if (configuration.has("caseInsensitive") && configuration.get("caseInsensitive").asBoolean()) {
                return Pattern.CASE_INSENSITIVE;
            }
        }

        return 0;
    }

    private String extactRegularExpression(JsonNode configuration) {
        if (configuration.isObject()) {
            if (!configuration.has("regex")) throw new RuntimeException("Missing regular expression");
            return configuration.get("regex").asText();
        } else {
            return configuration.asText();
        }
    }

    @Override
    public String mask(String identifier) {
        if ((isWhiteListEnabled && matchesAtLeastAPattern(identifier)) ||
                (!isWhiteListEnabled && !matchesAtLeastAPattern(identifier))) {
            return identifier;
        } else {
            return maskingProvider.mask(identifier);
        }
    }

    private boolean matchesAtLeastAPattern(String identifier) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(identifier).matches()) {
                return true;
            }
        }

        return false;
    }
}
