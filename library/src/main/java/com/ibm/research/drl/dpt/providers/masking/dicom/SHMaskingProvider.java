/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.RandomMaskingProvider;

import java.security.SecureRandom;

public class SHMaskingProvider extends AbstractMaskingProvider {
    private final RandomMaskingProvider randomMaskingProvider;

    public SHMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
    }

    public SHMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    @Override
    public String mask(String identifier) {
        return randomMaskingProvider.mask(identifier);
    }
}

