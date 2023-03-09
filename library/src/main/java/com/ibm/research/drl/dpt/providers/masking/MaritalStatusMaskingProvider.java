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
import com.ibm.research.drl.dpt.managers.MaritalStatusManager;
import com.ibm.research.drl.dpt.models.MaritalStatus;

import java.security.SecureRandom;

/**
 * The type Marital status masking provider.
 */
public class MaritalStatusMaskingProvider implements MaskingProvider {
    private static final MaritalStatusManager statusManager = MaritalStatusManager.getInstance();

    /**
     * Instantiates a new Marital status masking provider.
     */
    public MaritalStatusMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Marital status masking provider.
     *
     * @param configuration the configuration
     */
    public MaritalStatusMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Marital status masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public MaritalStatusMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
    }

    @Override
    public String mask(String identifier) {
        MaritalStatus maritalStatus = statusManager.getKey(identifier);
        if (maritalStatus == null) {
            return statusManager.getRandomKey();
        }

        return statusManager.getRandomKey(maritalStatus.getNameCountryCode());
    }
}
