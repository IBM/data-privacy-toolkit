/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CountyManager;
import com.ibm.research.drl.dpt.models.County;

import java.security.SecureRandom;

public class CountyMaskingProvider extends AbstractMaskingProvider {
    private final static CountyManager countyManager = CountyManager.getInstance();
    private final boolean getPseudorandom;

    /**
     * Instantiates a new Country masking provider.
     */
    public CountyMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public CountyMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CountyMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.getPseudorandom = configuration.getBooleanValue("county.mask.pseudorandom");
    }

    @Override
    public String mask(String identifier) {

        if (this.getPseudorandom) {
            return countyManager.getPseudorandom(identifier);
        }

        County county = countyManager.getKey(identifier);
        if (county == null) {
            return countyManager.getRandomKey();
        }

        return countyManager.getRandomKey(county.getNameCountryCode());
    }
}


