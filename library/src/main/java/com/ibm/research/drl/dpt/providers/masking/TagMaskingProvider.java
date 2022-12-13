/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;
import java.util.HashMap;

public class TagMaskingProvider extends AbstractMaskingProvider {
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

