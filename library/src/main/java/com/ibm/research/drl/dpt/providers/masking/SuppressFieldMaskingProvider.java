/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;

public class SuppressFieldMaskingProvider implements MaskingProvider {
    public SuppressFieldMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
    }

    @Override
    public String mask(String identifier) {
        return null;
    }
}

