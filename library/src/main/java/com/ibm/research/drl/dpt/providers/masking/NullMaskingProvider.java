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

import java.security.SecureRandom;

public class NullMaskingProvider implements MaskingProvider {

    private final boolean returnNull;

    /**
     * Instantiates a new Null masking provider.
     */
    public NullMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Null masking provider.
     *
     * @param random the random
     */
    public NullMaskingProvider(SecureRandom random) {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Null masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public NullMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    /**
     * Instantiates a new Null masking provider.
     *
     * @param configuration the configuration
     */
    public NullMaskingProvider(MaskingConfiguration configuration) {
        this.returnNull = configuration.getBooleanValue("null.mask.returnNull");
    }

    @Override
    public String mask(String identifier) {
        if (this.returnNull) {
            return null;
        }

        return "";
    }

    @Override
    public boolean supportsObject() {
        return true;
    }

    @Override
    public byte[] mask(Object complex, String fieldName) {
        if (this.returnNull) {
            return null;
        }

        return "".getBytes();
    }
}
