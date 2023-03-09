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
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.models.City;

import java.security.SecureRandom;

/**
 * The type City masking provider.
 */
public class CityMaskingProvider implements MaskingProvider {
    private static final CityManager cityManager = CityManager.getInstance();
    private final boolean getClosest;
    private final int closestK;
    private final boolean getPseudorandom;

    /**
     * Instantiates a new City masking provider.
     */
    public CityMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new City masking provider.
     *
     * @param configuration the configuration
     */
    public CityMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new City masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CityMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.getClosest = configuration.getBooleanValue("city.mask.closest");
        this.closestK = configuration.getIntValue("city.mask.closestK");
        this.getPseudorandom = configuration.getBooleanValue("city.mask.pseudorandom");
    }

    @Override
    public String mask(String identifier) {

        if (getPseudorandom) {
            return cityManager.getPseudorandom(identifier);
        }

        if (getClosest) {
            return cityManager.getClosestCity(identifier, this.closestK);
        }

        City city = cityManager.getKey(identifier);
        if (city == null) {
            return cityManager.getRandomKey();
        }

        return cityManager.getRandomKey(city.getNameCountryCode());
    }
}
