/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.ReligionManager;
import com.ibm.research.drl.dpt.models.Religion;

import java.security.SecureRandom;

/**
 * The type Religion masking provider.
 */
public class ReligionMaskingProvider extends AbstractMaskingProvider {
    private static final ReligionManager religionManager = ReligionManager.getInstance();
    private final boolean probabilityBasedMasking;
    
    /**
     * Instantiates a new Religion masking provider.
     */
    public ReligionMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Religion masking provider.
     *
     * @param configuration the configuration
     */
    public ReligionMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Religion masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public ReligionMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.probabilityBasedMasking = configuration.getBooleanValue("religion.mask.probabilityBased");
    }

    private String randomMask(Religion religion) {
        if (religion == null) {
            return religionManager.getRandomKey();
        }

        return religionManager.getRandomKey(religion.getNameCountryCode());
    }
    
    private String probabilisticMask(Religion religion) {
       
        if (religion == null) {
            return religionManager.getRandomProbabilityBased();
        }
        
        return religionManager.getRandomProbabilityBased(religion.getNameCountryCode());
    }
    
    @Override
    public String mask(String identifier) {
        Religion religion = religionManager.getKey(identifier);

        if (!this.probabilityBasedMasking) {
            return randomMask(religion);
        }
        
        return probabilisticMask(religion);
    }
}
