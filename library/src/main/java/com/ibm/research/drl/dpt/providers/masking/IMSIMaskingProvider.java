/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.IMSIManager;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

public class IMSIMaskingProvider implements MaskingProvider {
    private static final IMSIManager imsiManager = IMSIManager.getInstance();
    private final boolean preserveMCC;
    private final boolean preserveMNC;

    /**
     * Instantiates a new Imsi masking provider.
     */
    public IMSIMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imsi masking provider.
     *
     * @param random the random
     */
    public IMSIMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imsi masking provider.
     *
     * @param configuration the configuration
     */
    public IMSIMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Imsi masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IMSIMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.preserveMCC = configuration.getBooleanValue("imsi.mask.preserveMCC");
        this.preserveMNC = configuration.getBooleanValue("imsi.mask.preserveMNC");
    }

    @Override
    public String mask(String identifier) {
        if (!imsiManager.isValidIMSI(identifier)) {
            return RandomGenerators.generateRandomIMSI();
        }

        String mcc;
        String mnc;

        if (this.preserveMCC) {
            mcc = identifier.substring(0, 3);
            if (this.preserveMNC) {
                mnc = identifier.substring(3, 6);
            } else {
                mnc = imsiManager.getRandomMNC(mcc);
            }
        } else {
            mcc = imsiManager.getRandomMCC();
            mnc = imsiManager.getRandomMNC(mcc);
        }

        String uid = RandomGenerators.generateRandomDigitSequence(15 - mcc.length() - mnc.length());

        String builder = mcc +
                mnc +
                uid;

        return builder;
    }
}

