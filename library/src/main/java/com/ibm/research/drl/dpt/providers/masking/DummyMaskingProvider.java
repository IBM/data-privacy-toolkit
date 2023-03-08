/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

/**
 * The type Dummy masking provider.
 */
public class DummyMaskingProvider implements MaskingProvider {

    /**
     * Instantiates a new Dummy masking provider.
     */
    public DummyMaskingProvider() {

    }

    /**
     * Instantiates a new Dummy masking provider.
     *
     * @param configuration the configuration
     */
    public DummyMaskingProvider(MaskingConfiguration configuration) {
        this();
    }

    @Override
    public String mask(String identifier) {
        return identifier;
    }

    @Override
    public String maskEqual(String identifier, String equalValue) {
        return equalValue;
    }
}
