/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.NamesManager;
import com.ibm.research.drl.dpt.models.FirstName;
import com.ibm.research.drl.dpt.models.LastName;
import com.ibm.research.drl.dpt.util.FormatUtils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;

/**
 * The type Name masking provider.
 */
public class NameMaskingProvider extends AbstractComplexMaskingProvider<String> {
    private final NamesManager.Names names;
    private final boolean allowAnyGender;
    private final String separator;
    private final String whitespace;
    private final String virtualField;
    private final MaskingProvider virtualFieldMaskingProvider;
    private final boolean getPseudorandom;

    /**
     * Instantiates a new Name masking provider.
     *
     * @param configuration the configuration
     */
    public NameMaskingProvider(String complexType, MaskingConfiguration configuration, Set<String> maskedFields, MaskingProviderFactory factory) {
        super(complexType, configuration, maskedFields, factory);

        names = NamesManager.instance();
        this.allowAnyGender = configuration.getBooleanValue("names.masking.allowAnyGender");
        this.separator = configuration.getStringValue("names.masking.separator");
        this.whitespace = configuration.getStringValue("names.masking.whitespace");
        this.getPseudorandom = configuration.getBooleanValue("names.mask.pseudorandom");
        this.virtualField = configuration.getStringValue("names.mask.virtualField");

        this.random = new SecureRandom();

        if (this.virtualField != null) {
            MaskingConfiguration subConf = getConfigurationForSubfield(this.virtualField, configuration);
            this.virtualFieldMaskingProvider = getMaskingProvider(this.virtualField, subConf, this.factory);
        } else {
            this.virtualFieldMaskingProvider = null;
        }
    }

    /**
     * Instantiates a new Name masking provider.
     */
    public NameMaskingProvider(MaskingProviderFactory factory) {
        this("name", new DefaultMaskingConfiguration(), Collections.emptySet(), factory);
    }

    public NameMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("name", maskingConfiguration, Collections.emptySet(), factory);
    }

    public NameMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("name", maskingConfiguration, Collections.emptySet(), factory);
    }

    @Override
    public String mask(String identifier) {
        StringBuilder builder = new StringBuilder();

        String[] tokens = identifier.split(this.separator);

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            if (token.isEmpty()) continue;

            final String maskedToken;

            if (1 == token.length()) {
                if (Character.isAlphabetic(token.charAt(0))) {
                    // initial: randomize it
                    maskedToken = "" + ('A' + random.nextInt(26));
                } else {
                    // preserve it
                    maskedToken = token;
                }
            } else {
                FirstName lookup = names.getFirstName(token);
                if (lookup != null) {
                    if (getPseudorandom) {
                        maskedToken = names.getPseudoRandomFirstName(lookup.getGender(), allowAnyGender, token);
                    } else if (virtualField != null) {
                        maskedToken = virtualFieldMaskingProvider.mask(token);
                    } else {
                        maskedToken = names.getRandomFirstName(lookup.getGender(), allowAnyGender, lookup.getNameCountryCode());
                    }
                } else {
                    LastName lookupLastName = names.getLastName(token);
                    if (lookupLastName != null) {
                        if (getPseudorandom) {
                            maskedToken = names.getPseudoRandomLastName(token);
                        } else if (virtualField != null) {
                            maskedToken = virtualFieldMaskingProvider.mask(token);
                        } else {
                            maskedToken = names.getRandomLastName(lookupLastName.getNameCountryCode());
                        }
                    } else {
                        if (getPseudorandom) {
                            maskedToken = names.getPseudoRandomLastName(token);
                        } else if (virtualField != null) {
                            maskedToken = virtualFieldMaskingProvider.mask(token);
                        } else {
                            maskedToken = names.getRandomLastName();
                        }
                    }
                }
            }

            builder.append(this.virtualField == null ? FormatUtils.makeTitleCase(maskedToken) : maskedToken);

            if (i < (tokens.length - 1)) {
                builder.append(this.whitespace);
            }
        }

        return builder.toString().trim();
    }
}
