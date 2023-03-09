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
import com.ibm.research.drl.dpt.managers.ATCManager;
import com.ibm.research.drl.dpt.providers.identifiers.ATCIdentifier;

import java.security.SecureRandom;

public class ATCMaskingProvider implements MaskingProvider {
    private static final ATCIdentifier atcIdentifier = new ATCIdentifier();
    private static final ATCManager atcManager = ATCManager.getInstance();
    private final int prefixPreserveLength;

    /**
     * Instantiates a new Atc masking provider.
     */
    public ATCMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param random the random
     */
    public ATCMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public ATCMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public ATCMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        int levelsToKeep = maskingConfiguration.getIntValue("atc.mask.levelsToKeep");
        if (levelsToKeep == 1) {
            this.prefixPreserveLength = 1;
        } else if (levelsToKeep == 2) {
            this.prefixPreserveLength = 3;
        } else if (levelsToKeep == 3) {
            this.prefixPreserveLength = 4;
        } else if (levelsToKeep == 4) {
            this.prefixPreserveLength = 5;
        } else {
            this.prefixPreserveLength = 7;
        }
    }


    @Override
    public String mask(String identifier) {
        if (!atcIdentifier.isOfThisType(identifier) || prefixPreserveLength == 7) {
            return atcManager.getRandomKey();
        }

        return identifier.substring(0, prefixPreserveLength);
    }
}

