/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.MedicineManager;
import com.ibm.research.drl.dpt.models.Medicine;

import java.security.SecureRandom;

public class MedicineMaskingProvider implements MaskingProvider {
    private static final MedicineManager medicineManager = MedicineManager.getInstance();

    /**
     * Instantiates a new Medicine masking provider.
     */
    public MedicineMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Medicine masking provider.
     *
     * @param random the random
     */
    public MedicineMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Medicine masking provider.
     *
     * @param configuration the configuration
     */
    public MedicineMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Medicine masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public MedicineMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {

    }

    @Override
    public String mask(String identifier) {
        Medicine medicine = medicineManager.getKey(identifier);

        if (medicine == null) {
            return medicineManager.getRandomKey();
        }

        return medicineManager.getRandomKey(medicine.getNameCountryCode());
    }
}

