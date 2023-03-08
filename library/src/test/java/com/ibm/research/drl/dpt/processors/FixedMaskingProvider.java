/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.security.SecureRandom;

public class FixedMaskingProvider implements MaskingProvider {

    public FixedMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {}

    @Override
    public String mask(String identifier) {
        return "FIXED";
    }
}

