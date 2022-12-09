/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.models.Race;

import java.security.SecureRandom;

/**
 * The type Race ethnicity masking provider.
 *
 * @author stefanob
 */
public class RaceEthnicityMaskingProvider extends AbstractMaskingProvider {
    private static final RaceManager raceManager = RaceManager.getInstance();
    private final boolean probabilityBasedMasking;
    
    /**
     * Instantiates a new Race ethnicity masking provider.
     */
    public RaceEthnicityMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Race ethnicity masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public RaceEthnicityMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Race ethnicity masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public RaceEthnicityMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.probabilityBasedMasking = configuration.getBooleanValue("race.mask.probabilityBased");
    }

    private String randomMask(Race race) {
        if (race == null) {
            return raceManager.getRandomKey();
        }

        return raceManager.getRandomKey(race.getNameCountryCode());
    }

    private String probabilisticMask(Race race) {
        if (race == null) {
            return raceManager.getRandomProbabilityBased();
        }

        return raceManager.getRandomProbabilityBased(race.getNameCountryCode());
    }
    
    @Override
    public String mask(String identifier) {
        Race race = raceManager.getKey(identifier);

        if (!this.probabilityBasedMasking) {
            return randomMask(race);
        }

        return probabilisticMask(race); 
    }
}
