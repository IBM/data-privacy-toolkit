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
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.models.Race;

import java.security.SecureRandom;

/**
 * The type Race ethnicity masking provider.
 *
 */
public class RaceEthnicityMaskingProvider implements MaskingProvider {
    private static final RaceManager raceManager = RaceManager.getInstance();
    private final boolean probabilityBasedMasking;

    /**
     * Instantiates a new Race ethnicity masking provider.
     */
    public RaceEthnicityMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Race ethnicity masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public RaceEthnicityMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Race ethnicity masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public RaceEthnicityMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.probabilityBasedMasking = configuration.getBooleanValue("race.mask.probabilityBased");
    }

    private String randomMask(Race race) {
        if (race == null) {
            return raceManager.getRandomKey();
        }

        return raceManager.getRandomKey(race.getNameCountryCode());
    }

    private String probabilisticMask(Race race) {
        if (race == null) {
            return raceManager.getRandomProbabilityBased();
        }

        return raceManager.getRandomProbabilityBased(race.getNameCountryCode());
    }

    @Override
    public String mask(String identifier) {
        Race race = raceManager.getKey(identifier);

        if (!this.probabilityBasedMasking) {
            return randomMask(race);
        }

        return probabilisticMask(race);
    }
}
