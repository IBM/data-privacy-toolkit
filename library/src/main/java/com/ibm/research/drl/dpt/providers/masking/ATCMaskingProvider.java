/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.ATCManager;
import com.ibm.research.drl.dpt.providers.identifiers.ATCIdentifier;

import java.security.SecureRandom;

public class ATCMaskingProvider extends AbstractMaskingProvider {
    private static final ATCIdentifier atcIdentifier = new ATCIdentifier();
    private static final ATCManager atcManager = ATCManager.getInstance();
    private final int levelsToKeep;
    private final int prefixPreserveLength;

    /**
     * Instantiates a new Atc masking provider.
     */
    public ATCMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param random the random
     */
    public ATCMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public ATCMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Atc masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public ATCMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.levelsToKeep = maskingConfiguration.getIntValue("atc.mask.levelsToKeep");
        if (this.levelsToKeep == 1) {
            this.prefixPreserveLength = 1;
        }
        else if (this.levelsToKeep == 2) {
            this.prefixPreserveLength = 3;
        }
        else if (this.levelsToKeep == 3) {
            this.prefixPreserveLength = 4;
        }
        else if (this.levelsToKeep == 4) {
            this.prefixPreserveLength = 5;
        }
        else {
            this.prefixPreserveLength = 7;
        }
    }


    @Override
    public String mask(String identifier) {
        if (!atcIdentifier.isOfThisType(identifier) || prefixPreserveLength == 7) {
            return atcManager.getRandomKey();
        }

        return identifier.substring(0, prefixPreserveLength);

    }
}

