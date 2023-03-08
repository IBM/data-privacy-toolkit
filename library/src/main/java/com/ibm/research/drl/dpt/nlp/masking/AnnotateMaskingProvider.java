/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.security.SecureRandom;
import java.util.HashMap;

public class AnnotateMaskingProvider implements MaskingProvider {
    public AnnotateMaskingProvider(SecureRandom secureRandom, MaskingConfiguration maskingConfiguration) {}

    private static class CacheEntry extends HashMap<String, String> {}
    private static class Cache extends HashMap<String, CacheEntry> {}

    private static final Cache cache = new Cache();

    @Override
    public String mask(String identifier) {
        return mask(identifier, "UNKNOWN");
    }

    @Override
    public String mask(String identifier, String fieldName) {
        return "<ProviderType:" + fieldName + ">" + identifier + "</ProviderType>";
    }

}

