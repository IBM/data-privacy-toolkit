/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
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
