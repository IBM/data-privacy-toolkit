/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocallyPersistentMaskingProvider extends AbstractPersistentMaskingProvider {
    private final Map<String, String> persistenceProvider;

    /**
     * Instantiates a new Locally persistent masking provider.
     *
     * @param notPersistentMasking the not persistent masking provider
     * @param configuration
     */
    public LocallyPersistentMaskingProvider(MaskingProvider notPersistentMasking, MaskingConfiguration configuration) {
        super(notPersistentMasking, configuration);
        this.persistenceProvider = new ConcurrentHashMap<>();
    }

    @Override
    protected boolean isCached(String value) {
        return persistenceProvider.containsKey(value);
    }

    @Override
    protected String getCachedValue(String value) {
        return persistenceProvider.get(value);
    }

    @Override
    protected void cacheValue(String value, String maskedValue) {
        persistenceProvider.putIfAbsent(value, maskedValue);
    }
}
