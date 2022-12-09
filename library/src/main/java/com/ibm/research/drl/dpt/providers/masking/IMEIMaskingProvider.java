/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.IMEIManager;
import com.ibm.research.drl.dpt.providers.identifiers.IMEIIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

public class IMEIMaskingProvider extends AbstractMaskingProvider {
    private static final IMEIIdentifier IMEI_IDENTIFIER = new IMEIIdentifier();
    private static final IMEIManager imeiManager = IMEIManager.getInstance();
    private final boolean preserveTAC;

    /**
     * Instantiates a new Imei masking provider.
     */
    public IMEIMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param random the random
     */
    public IMEIMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param configuration the configuration
     */
    public IMEIMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Imei masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IMEIMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveTAC = configuration.getBooleanValue("imei.mask.preserveTAC");
    }

    @Override
    public String mask(String identifier) {
        String tac;
        if (!IMEI_IDENTIFIER.isOfThisType(identifier) || !this.preserveTAC) {
            tac = imeiManager.getRandomKey();
        } else {
            tac = identifier.substring(0, 8);
        }

        String body = tac + RandomGenerators.generateRandomDigitSequence(6);
        body += (char) ('0' + RandomGenerators.luhnCheckDigit(body));

        return body;
    }
}
