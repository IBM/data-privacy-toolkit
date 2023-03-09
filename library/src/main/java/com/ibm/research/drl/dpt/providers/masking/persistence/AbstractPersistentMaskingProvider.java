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
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

public abstract class AbstractPersistentMaskingProvider implements MaskingProvider {
    private final MaskingProvider maskingProvider;
    private final boolean normalizeToLowerCase;

    public enum PersistencyType {
        MEMORY,
        FILE,
        DATABASE,
        CAUSAL
    }

    public AbstractPersistentMaskingProvider(MaskingProvider maskingProvider, MaskingConfiguration configuration) {
        this.maskingProvider = maskingProvider;
        this.normalizeToLowerCase = configuration.getBooleanValue("persistence.normalize.toLower");
    }

    @Override
    public final String mask(final String value) {
        return mask(value, "");
    }

    @Override
    public final String mask(String identifier, final String fieldName) throws IllegalArgumentException, NullPointerException {
        identifier = transformIfRequired(identifier);
        if (!isCached(identifier)) {
            String maskedValue = maskingProvider.mask(identifier, fieldName);
            cacheValue(identifier, maskedValue);
        }

        return getCachedValue(identifier);

    }

    private String transformIfRequired(String identifier) {
        if (normalizeToLowerCase) {
            return identifier.toLowerCase();
        }
        return identifier;
    }

    protected abstract boolean isCached(String value);

    protected abstract String getCachedValue(String value);

    protected abstract void cacheValue(String value, String maskedValue);
}
