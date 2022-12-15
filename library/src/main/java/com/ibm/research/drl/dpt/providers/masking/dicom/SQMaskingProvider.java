/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractMaskingProvider;

import java.security.SecureRandom;

public class SQMaskingProvider extends AbstractMaskingProvider {
    @Override
    public String mask(String identifier) {
        return null;
    }

    public SQMaskingProvider(MaskingConfiguration maskingConfiguration) {
    }

    public SQMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

}

