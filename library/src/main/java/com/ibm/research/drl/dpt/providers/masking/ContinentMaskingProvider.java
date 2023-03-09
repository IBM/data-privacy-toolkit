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
import com.ibm.research.drl.dpt.managers.ContinentManager;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.Continent;
import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.security.SecureRandom;

/**
 * The type Continent masking provider.
 *
 */
public class ContinentMaskingProvider implements MaskingProvider {
    private static final CountryManager countryManager = CountryManager.getInstance();
    private static final CityManager cityManager = CityManager.getInstance();
    private final boolean getClosest;
    private final int getClosestK;

    private final static ContinentManager continentManager = ContinentManager.getInstance();

    /**
     * Instantiates a new Continent masking provider.
     */
    public ContinentMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Continent masking provider.
     *
     * @param configuration the configuration
     */
    public ContinentMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Continent masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public ContinentMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.getClosest = configuration.getBooleanValue("continent.mask.closest");
        this.getClosestK = configuration.getIntValue("continent.mask.closestK");
    }

    @Override
    public String maskLinked(String identifier, String maskedValue, ProviderType providerType) {
        if (null == providerType) {
            providerType = ProviderType.COUNTRY;
        }

        if (providerType == ProviderType.COUNTRY) {
            Continent continent = continentManager.getKey(identifier);
            String locale = null;
            if (continent != null) {
                locale = continent.getNameCountryCode();
            }

            Country country = countryManager.lookupCountry(maskedValue, locale);
            if (country == null) {
                return mask(identifier);
            }

            return country.getContinent();
        } else if (providerType == ProviderType.CITY) {
            City city = cityManager.getKey(maskedValue);
            if (city != null) {
                String countryCode = city.getCountryCode();
                Country country = countryManager.lookupCountry(countryCode, city.getNameCountryCode());
                if (country != null) {
                    return country.getContinent();
                }
            }

            return mask(identifier);
        }

        return mask(identifier);
    }

    @Override
    public String mask(String identifier) {

        if (getClosest) {
            return continentManager.getClosestContinent(identifier, getClosestK);
        }

        Continent continent = continentManager.getKey(identifier);
        if (continent == null) {
            return continentManager.getRandomKey();
        }

        return continentManager.getRandomKey(continent.getNameCountryCode());
    }
}
