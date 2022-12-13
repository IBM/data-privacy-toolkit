/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.models.City;

import java.security.SecureRandom;

/**
 * The type City masking provider.
 */
public class CityMaskingProvider extends AbstractMaskingProvider {
    private static final CityManager cityManager = CityManager.getInstance();
    private final boolean getClosest;
    private final int closestK;
    private final boolean getPseudorandom;

    /**
     * Instantiates a new City masking provider.
     */
    public CityMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new City masking provider.
     *
     * @param configuration the configuration
     */
    public CityMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new City masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CityMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.getClosest = configuration.getBooleanValue("city.mask.closest");
        this.closestK = configuration.getIntValue("city.mask.closestK");
        this.getPseudorandom = configuration.getBooleanValue("city.mask.pseudorandom");
    }

    @Override
    public String mask(String identifier) {

        if (getPseudorandom) {
            return cityManager.getPseudorandom(identifier);
        }

        if (getClosest) {
            return cityManager.getClosestCity(identifier, this.closestK);
        }

        City city = cityManager.getKey(identifier);
        if (city == null) {
            return cityManager.getRandomKey();
        }

        return cityManager.getRandomKey(city.getNameCountryCode());
    }
}
