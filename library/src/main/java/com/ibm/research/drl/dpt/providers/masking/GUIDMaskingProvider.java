/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;
import java.util.UUID;

public class GUIDMaskingProvider implements MaskingProvider {

    /**
     * Instantiates a new Guid masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public GUIDMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
    }

    @Override
    public String mask(String identifier) {
        return UUID.randomUUID().toString();
    }
}
