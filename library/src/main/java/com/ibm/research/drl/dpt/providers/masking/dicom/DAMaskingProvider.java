/******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.DateTimeMaskingProvider;

import java.security.SecureRandom;

public class DAMaskingProvider extends AbstractMaskingProvider {
    private final DateTimeMaskingProvider dateTimeMaskingProvider;

    /**
     * Instantiates a new Da masking provider.
     */
    public DAMaskingProvider(MaskingConfiguration maskingConfiguration) {
        dateTimeMaskingProvider = new DateTimeMaskingProvider(maskingConfiguration);
    }

    public DAMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    @Override
    public String mask(String identifier) {
        return dateTimeMaskingProvider.mask(identifier);
    }
}

