/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.DateTimeMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.security.SecureRandom;

public class DTMaskingProvider implements MaskingProvider {
    private final DateTimeMaskingProvider dateTimeMaskingProvider;

    /**
     * Instantiates a new Dt masking provider.
     */
    public DTMaskingProvider(MaskingConfiguration maskingConfiguration) {
        maskingConfiguration.setValue("datetime.format.fixed", "yyyyMMddHHmmss.SSSSZ");
        dateTimeMaskingProvider = new DateTimeMaskingProvider(maskingConfiguration);
    }

    public DTMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    @Override
    public String mask(String identifier) {
        return dateTimeMaskingProvider.mask(identifier);
    }
}

