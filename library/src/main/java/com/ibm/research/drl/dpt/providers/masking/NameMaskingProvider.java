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
    private final SecureRandom random;

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
