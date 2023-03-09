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


public class MonetaryMaskingProvider implements MaskingProvider {
    private final String replacingCharacter;
    private final boolean preserveSize;

    public MonetaryMaskingProvider(MaskingConfiguration configuration) {

        replacingCharacter = configuration.getStringValue("monetary.replacing.character");
        preserveSize = configuration.getBooleanValue("monetary.preserve.size");
    }

    @Override
    public String mask(String identifier) {
        final StringBuilder builder = new StringBuilder();

        if (preserveSize) {
            for (final char c : identifier.toCharArray()) {
                if (Character.isDigit(c)) {
                    builder.append(replacingCharacter);
                } else {
                    builder.append(c);
                }
            }
        } else {
            for (final char c : identifier.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    builder.append(c);
                }
            }
        }

        return builder.toString();
    }
}
