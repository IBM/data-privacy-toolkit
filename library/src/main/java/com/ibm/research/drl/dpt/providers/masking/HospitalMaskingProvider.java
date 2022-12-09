/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.HospitalManager;
import com.ibm.research.drl.dpt.models.Hospital;

import java.security.SecureRandom;

public class HospitalMaskingProvider extends AbstractMaskingProvider {
    private final static HospitalManager hospitalManager = HospitalManager.getInstance();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Hospital masking provider.
     */
    public HospitalMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param random the random
     */
    public HospitalMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param configuration the configuration
     */
    public HospitalMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Hospital masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public HospitalMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.preserveCountry = maskingConfiguration.getBooleanValue("hospital.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (!this.preserveCountry) {
            return hospitalManager.getRandomKey();
        }

        Hospital hospital = hospitalManager.getKey(identifier);
        if (hospital == null) {
            return hospitalManager.getRandomKey();
        }

        return hospitalManager.getRandomKey(hospital.getNameCountryCode());
    }
}
