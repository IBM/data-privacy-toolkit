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
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.security.SecureRandom;

/**
 * The type Country masking provider.
 *
 */
public class CountryMaskingProvider implements MaskingProvider {
    private static final CountryManager countryManager = CountryManager.getInstance();
    private final boolean getClosest;
    private final int closestK;
    private final boolean getPseudorandom;
    private static final CityManager cityManager = CityManager.getInstance();

    /**
     * Instantiates a new Country masking provider.
     */
    public CountryMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public CountryMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CountryMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.getClosest = configuration.getBooleanValue("country.mask.closest");
        this.closestK = configuration.getIntValue("country.mask.closestK");
        this.getPseudorandom = configuration.getBooleanValue("country.mask.pseudorandom");
    }

    @Override
    public String maskLinked(String identifier, String linkedValue, ProviderType linkedType) {
        City city = cityManager.getKey(linkedValue);
        if (city == null) {
            return mask(identifier);
        }

        Country country = countryManager.lookupCountry(city.getCountryCode(), city.getNameCountryCode());
        if (country == null) {
            return mask(identifier);
        }

        return country.getName();
    }

    @Override
    public String mask(String identifier) {

        if (this.getPseudorandom) {
            return countryManager.getPseudorandom(identifier);
        }

        if (getClosest) {
            return countryManager.getClosestCountry(identifier, this.closestK);
        }

        Country country = countryManager.lookupCountry(identifier);

        if (country == null) {
            return countryManager.getRandomKey();
        } else {
            return countryManager.getRandomKey(identifier, country.getNameCountryCode());
        }
    }
}
