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

public class CSMaskingProvider extends AbstractMaskingProvider {
    private final RandomMaskingProvider randomMaskingProvider;
    private final char[] genders = "FMO".toCharArray();
    private final String[] sexNeutered = {"ALTERED", "UNALTERED"};

    private final DicomEntityType entityType;

    /**
     * Instantiates a new Cs masking provider.
     *
     */
    public CSMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    public CSMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
        this.entityType = DicomEntityType.valueOf(maskingConfiguration.getStringValue("dicom.cs.entityType"));
        this.random = random;
    }

    @Override
    public String mask(String identifier) {
        switch (entityType) {
            case GENDER:
                return "" + genders[random.nextInt(genders.length)];
            case SEX_NEUTERED:
                return sexNeutered[random.nextInt(sexNeutered.length)];
        }

        return randomMaskingProvider.mask(identifier);
    }

    public String toString() {
        return "CS," + this.entityType.toString();
    }
}

