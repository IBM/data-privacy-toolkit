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
import com.ibm.research.drl.dpt.managers.SWIFTCodeManager;

import java.security.SecureRandom;

public class SWIFTCodeMaskingProvider implements MaskingProvider {
    private static final SWIFTCodeManager swiftCodeManager = SWIFTCodeManager.getInstance();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Swift code masking provider.
     */
    public SWIFTCodeMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param random the random
     */
    public SWIFTCodeMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public SWIFTCodeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public SWIFTCodeMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.preserveCountry = maskingConfiguration.getBooleanValue("swift.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (this.preserveCountry) {
            return swiftCodeManager.getCodeFromCountry(identifier);
        }

        return swiftCodeManager.getRandomKey();
    }
}

