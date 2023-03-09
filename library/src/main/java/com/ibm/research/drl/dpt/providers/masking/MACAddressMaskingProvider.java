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
import com.ibm.research.drl.dpt.providers.identifiers.MACAddressIdentifier;

import java.security.SecureRandom;

public class MACAddressMaskingProvider implements MaskingProvider {
    private static final char[] allowedCharacters = "abcdef0123456789".toCharArray();
    private static final MACAddressIdentifier macAddressIdentifier = new MACAddressIdentifier();
    private final boolean preserveVendor;
    private final SecureRandom random;

    /**
     * Instantiates a new Mac address masking provider.
     */
    public MACAddressMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param configuration the configuration
     */
    public MACAddressMaskingProvider(DefaultMaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param random the random
     */
    public MACAddressMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public MACAddressMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveVendor = configuration.getBooleanValue("mac.masking.preserveVendor");
    }

    private String randomMACAddress(int octets) {
        int subsetLength = allowedCharacters.length;

        StringBuilder builder = new StringBuilder((octets - 1) * 3 + 2);
        for (int i = 0; i < (octets - 1); i++) {
            builder.append(allowedCharacters[random.nextInt(subsetLength)]);
            builder.append(allowedCharacters[random.nextInt(subsetLength)]);
            builder.append(':');
        }

        builder.append(allowedCharacters[random.nextInt(subsetLength)]);
        builder.append(allowedCharacters[random.nextInt(subsetLength)]);

        return builder.toString();
    }

    @Override
    public String mask(String identifier) {
        if (!preserveVendor) {
            return randomMACAddress(6);
        }

        if (!macAddressIdentifier.isOfThisType(identifier)) {
            return randomMACAddress(6);
        }

        return identifier.substring(0, 9) + randomMACAddress(3);
    }
}

