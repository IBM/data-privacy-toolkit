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
import com.ibm.research.drl.dpt.managers.IMEIManager;
import com.ibm.research.drl.dpt.providers.identifiers.IMEIIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

public class IMEIMaskingProvider implements MaskingProvider {
    private static final IMEIIdentifier IMEI_IDENTIFIER = new IMEIIdentifier();
    private static final IMEIManager imeiManager = IMEIManager.getInstance();
    private final boolean preserveTAC;

    /**
     * Instantiates a new Imei masking provider.
     */
    public IMEIMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param random the random
     */
    public IMEIMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param configuration the configuration
     */
    public IMEIMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IMEIMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.preserveTAC = configuration.getBooleanValue("imei.mask.preserveTAC");
    }

    @Override
    public String mask(String identifier) {
        String tac;
        if (!IMEI_IDENTIFIER.isOfThisType(identifier) || !this.preserveTAC) {
            tac = imeiManager.getRandomKey();
        } else {
            tac = identifier.substring(0, 8);
        }

        String body = tac + RandomGenerators.generateRandomDigitSequence(6);
        body += (char) ('0' + RandomGenerators.luhnCheckDigit(body));

        return body;
    }
}
