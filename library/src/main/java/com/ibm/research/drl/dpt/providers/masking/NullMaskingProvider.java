/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;

public class NullMaskingProvider extends AbstractMaskingProvider {

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
