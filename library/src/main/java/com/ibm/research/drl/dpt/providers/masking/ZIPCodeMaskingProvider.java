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
import com.ibm.research.drl.dpt.managers.ZIPCodeManager;

/** Masking provider for US ZIP code values. */
public class ZIPCodeMaskingProvider implements MaskingProvider {
    /** The country code used to look up ZIP code data. */
    private final String countryCode;
    /** The minimum population threshold applied when masking. */
    private final int minimumPopulation;
    /** Whether to enforce a minimum population requirement when masking. */
    private final boolean requireMinimumPopulation;
    /** Whether to use a prefix-based population check. */
    private final boolean requireMinimumPopulationUsePrefix;
    /** The number of prefix digits to use for the prefix-based population check. */
    private final int requireMinimumPopulationPrefixDigits;

    /** Shared ZIP code manager instance. */
    private final ZIPCodeManager zipCodeManager;
    /** Fallback random masking provider for when no valid ZIP code can be found. */
    private final MaskingProvider randomMaskingProvider;

    /** Constructs a ZIPCodeMaskingProvider with default configuration. */
    public ZIPCodeMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Constructs a ZIPCodeMaskingProvider with the given configuration.
     *
     * @param maskingConfiguration the masking configuration
     */
    public ZIPCodeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this.countryCode = maskingConfiguration.getStringValue("zipcode.mask.countryCode");
        this.requireMinimumPopulation = maskingConfiguration.getBooleanValue("zipcode.mask.requireMinimumPopulation");
        this.requireMinimumPopulationUsePrefix = maskingConfiguration.getBooleanValue("zipcode.mask.minimumPopulationUsePrefix");
        this.requireMinimumPopulationPrefixDigits = maskingConfiguration.getIntValue("zipcode.mask.minimumPopulationPrefixDigits");
        this.minimumPopulation = maskingConfiguration.getIntValue("zipcode.mask.minimumPopulation");

        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
        this.zipCodeManager = new ZIPCodeManager(this.requireMinimumPopulationPrefixDigits);
    }

    @Override
    public String mask(String identifier) {

        if (requireMinimumPopulation) {

            if (this.requireMinimumPopulationUsePrefix && (identifier.length() >= this.requireMinimumPopulationPrefixDigits)) {
                identifier = identifier.substring(0, this.requireMinimumPopulationPrefixDigits);
            }

            int population;

            if (this.requireMinimumPopulationUsePrefix) {
                population = zipCodeManager.getPopulationByPrefix(countryCode, identifier);
            } else {
                population = zipCodeManager.getPopulation(countryCode, identifier);
            }

            if (population < minimumPopulation) {
                return "000";
            }

            return identifier;
        }

        String randomZip = zipCodeManager.getRandomKey(countryCode);
        if (randomZip == null) {
            return randomMaskingProvider.mask(identifier);
        }

        return randomZip;
    }
}


