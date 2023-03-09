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
import java.util.HashMap;

public class TagMaskingProvider implements MaskingProvider {
    private static class CacheEntry extends HashMap<String, String> {
    }

    private static class Cache extends HashMap<String, CacheEntry> {
    }

    private static final Cache cache = new Cache();

    public TagMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
    }

    @Override
    public String mask(String identifier) {
        return mask(identifier, "UNKNOWN");
    }

    @Override
    public String mask(String identifier, String fieldName) {
        synchronized (this) {
            final CacheEntry typeCache = cache.computeIfAbsent(fieldName, k -> new CacheEntry());
            return typeCache.computeIfAbsent(identifier, k -> fieldName + "-" + typeCache.size());
        }
    }
}

