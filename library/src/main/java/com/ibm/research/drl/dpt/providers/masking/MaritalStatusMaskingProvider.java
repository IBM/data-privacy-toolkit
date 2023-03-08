/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.MaritalStatusManager;
import com.ibm.research.drl.dpt.models.MaritalStatus;

import java.security.SecureRandom;

/**
 * The type Marital status masking provider.
 */
public class MaritalStatusMaskingProvider implements MaskingProvider {
    private static final MaritalStatusManager statusManager = MaritalStatusManager.getInstance();

    /**
     * Instantiates a new Marital status masking provider.
     */
    public MaritalStatusMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Marital status masking provider.
     *
     * @param configuration the configuration
     */
    public MaritalStatusMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Marital status masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public MaritalStatusMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
    }

    @Override
    public String mask(String identifier) {
        MaritalStatus maritalStatus = statusManager.getKey(identifier);
        if (maritalStatus == null) {
            return statusManager.getRandomKey();
        }

        return statusManager.getRandomKey(maritalStatus.getNameCountryCode());
    }
}
