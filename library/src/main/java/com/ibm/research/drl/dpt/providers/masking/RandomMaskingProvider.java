/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

public class RandomMaskingProvider implements MaskingProvider {

    /**
     * Instantiates a new Random masking provider.
     */
    public RandomMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Random masking provider.
     *
     * @param random the random
     */
    public RandomMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Random masking provider.
     *
     * @param configuration the configuration
     */
    public RandomMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Random masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public RandomMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
    }

    @Override
    public String mask(String identifier) {
        return RandomGenerators.randomReplacement(identifier);
    }
}

