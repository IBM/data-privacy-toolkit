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
import com.ibm.research.drl.dpt.managers.HospitalManager;
import com.ibm.research.drl.dpt.models.Hospital;

import java.security.SecureRandom;

public class HospitalMaskingProvider implements MaskingProvider {
    private final static HospitalManager hospitalManager = HospitalManager.getInstance();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Hospital masking provider.
     */
    public HospitalMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param random the random
     */
    public HospitalMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param configuration the configuration
     */
    public HospitalMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public HospitalMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.preserveCountry = maskingConfiguration.getBooleanValue("hospital.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (!this.preserveCountry) {
            return hospitalManager.getRandomKey();
        }

        Hospital hospital = hospitalManager.getKey(identifier);
        if (hospital == null) {
            return hospitalManager.getRandomKey();
        }

        return hospitalManager.getRandomKey(hospital.getNameCountryCode());
    }
}
