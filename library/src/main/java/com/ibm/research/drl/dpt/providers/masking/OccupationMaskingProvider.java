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
import com.ibm.research.drl.dpt.managers.OccupationManager;
import com.ibm.research.drl.dpt.models.Occupation;

import java.security.SecureRandom;
import java.util.List;

public class OccupationMaskingProvider implements MaskingProvider {
    /**
     * The constant occupationManager.
     */
    public static final OccupationManager occupationManager = OccupationManager.getInstance();
    private final boolean generalizeToCategory;
    private final SecureRandom random;

    /**
     * Instantiates a new Occupation masking provider.
     */
    public OccupationMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Occupation masking provider.
     *
     * @param configuration the configuration
     */
    public OccupationMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Occupation masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public OccupationMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.generalizeToCategory = configuration.getBooleanValue("occupation.mask.generalize");
        this.random = random;
    }


    @Override
    public String mask(String identifier) {
        Occupation occupation = occupationManager.getKey(identifier);

        if (this.generalizeToCategory) {
            if (occupation == null) {
                /* TODO: return a random category */
                return occupationManager.getRandomKey();
            }
            List<String> categories = occupation.getCategories();
            int randomIndex = random.nextInt(categories.size());
            return categories.get(randomIndex);
        } else {
            if (occupation == null) {
                return occupationManager.getRandomKey();
            }

            return occupationManager.getRandomKey(occupation.getNameCountryCode());
        }
    }
}

