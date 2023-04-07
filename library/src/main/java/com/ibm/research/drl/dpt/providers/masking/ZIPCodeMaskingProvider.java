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

public class ZIPCodeMaskingProvider implements MaskingProvider {
    private final String countryCode;
    private final int minimumPopulation;
    private final boolean requireMinimumPopulation;
    private final boolean requireMinimumPopulationUsePrefix;
    private final int requireMinimumPopulationPrefixDigits;

    private final ZIPCodeManager zipCodeManager;
    private final MaskingProvider randomMaskingProvider;

    public ZIPCodeMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

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


