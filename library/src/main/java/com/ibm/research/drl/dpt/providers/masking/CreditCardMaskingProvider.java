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
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

/**
 * The type Credit card masking provider.
 */
public class CreditCardMaskingProvider implements MaskingProvider {
    private final boolean preserveIssuer;
    private final SecureRandom random;
    private int preservedDigits = 0;

    /**
     * Instantiates a new Credit card masking provider.
     */
    public CreditCardMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param configuration the configuration
     */
    public CreditCardMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param random the random
     */
    public CreditCardMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CreditCardMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;

        this.preserveIssuer = configuration.getBooleanValue("creditCard.issuer.preserve");
        if (preserveIssuer) {
            this.preservedDigits = 6;
        }
    }

    @Override
    public String mask(String identifier) {
        if (!preserveIssuer) {
            return RandomGenerators.generateRandomCreditCard();
        }

        final StringBuilder buffer = new StringBuilder();

        int digitsEncountered = 0;
        int identifierLength = identifier.length();
        for (int i = 0; i < identifierLength; i++) {
            char c = identifier.charAt(i);

            if (Character.isDigit(c)) {
                digitsEncountered++;

                if (digitsEncountered > preservedDigits) {
                    if (i < (identifierLength - 1)) {
                        c = RandomGenerators.randomDigit();
                    } else {
                        c = (char) ('0' + RandomGenerators.luhnCheckDigit(buffer.toString().replaceAll("[\\- ]", "")));
                    }
                }
            }

            buffer.append(c);
        }

        return buffer.toString();
    }

}
