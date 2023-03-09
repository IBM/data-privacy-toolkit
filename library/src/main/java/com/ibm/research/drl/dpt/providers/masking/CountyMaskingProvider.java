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
import com.ibm.research.drl.dpt.managers.CountyManager;
import com.ibm.research.drl.dpt.models.County;

import java.security.SecureRandom;

public class CountyMaskingProvider implements MaskingProvider {
    private final static CountyManager countyManager = CountyManager.getInstance();
    private final boolean getPseudorandom;

    /**
     * Instantiates a new Country masking provider.
     */
    public CountyMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public CountyMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CountyMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.getPseudorandom = configuration.getBooleanValue("county.mask.pseudorandom");
    }

    @Override
    public String mask(String identifier) {

        if (this.getPseudorandom) {
            return countyManager.getPseudorandom(identifier);
        }

        County county = countyManager.getKey(identifier);
        if (county == null) {
            return countyManager.getRandomKey();
        }

        return countyManager.getRandomKey(county.getNameCountryCode());
    }
}


