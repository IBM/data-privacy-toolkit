/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;

public class BooleanMaskingProvider implements MaskingProvider {

    private final SecureRandom random;

    /**
     * Instantiates a new Number variance masking provider.
     */
    public BooleanMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Number variance masking provider.
     *
     * @param configuration the configuration
     */
    public BooleanMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Number variance masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public BooleanMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
    }

    @Override
    public String mask(String identifier) {
        return String.valueOf(random.nextBoolean());
    }
}

