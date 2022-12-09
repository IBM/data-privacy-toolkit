/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.*;

import java.security.SecureRandom;

public class LOMaskingProvider extends AbstractMaskingProvider {
    private final RandomMaskingProvider randomMaskingProvider;
    private final MaskingProvider nameMaskingProvider;
    private final MaskingProvider hospitalMaskingProvider;

    private final DicomEntityType entityType;

    /**
     * Instantiates a new Lo masking provider.
     *
     */
    public LOMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
        this.nameMaskingProvider = new NameMaskingProvider(maskingConfiguration, factory);
        this.hospitalMaskingProvider = new HospitalMaskingProvider(maskingConfiguration);
        this.entityType = DicomEntityType.valueOf(maskingConfiguration.getStringValue("dicom.lo.entityType"));
    }

    public LOMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this(maskingConfiguration, factory);
    }

    @Override
    public String mask(String identifier) {
        switch(entityType) {
            case HOSPITAL:
                return hospitalMaskingProvider.mask(identifier);
            case NAME:
                return nameMaskingProvider.mask(identifier);
            case GENERIC:
                return randomMaskingProvider.mask(identifier);
        }

        return randomMaskingProvider.mask(identifier);
    }

    public String toString() {
        return "LO," + this.entityType.toString();
    }
}

