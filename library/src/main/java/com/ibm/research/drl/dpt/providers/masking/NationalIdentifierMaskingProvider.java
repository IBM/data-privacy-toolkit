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

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;

/**
 * The type National identifier masking provider.
 */
public class NationalIdentifierMaskingProvider implements MaskingProvider {
    private final SecureRandom random;

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param random the random
     */
    public NationalIdentifierMaskingProvider(SecureRandom random) {
        this.random = random;
    }

    /**
     * Instantiates a new National identifier masking provider.
     */
    public NationalIdentifierMaskingProvider() {
        this(new SecureRandom());
    }

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param configuration the configuration
     */
    public NationalIdentifierMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom());
    }

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public NationalIdentifierMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this(random);
    }

    @Override
    public String mask(String identifier) {
        StringBuilder builder = new StringBuilder(identifier.length());
        int identifierLength = identifier.length();

        for (int i = 0; i < identifierLength; ++i) {
            char c = identifier.charAt(i);

            if (Character.isDigit(c)) {
                c = (char) ('0' + random.nextInt(9));
            } else if (Character.isLetter(c)) {
                c = (char) ((Character.isLowerCase(c) ? 'a' : 'A') + random.nextInt(26));
            }

            builder.append(c);
        }

        return builder.toString();
    }
}
