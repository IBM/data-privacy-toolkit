/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.SWIFTCodeManager;

import java.security.SecureRandom;

public class SWIFTCodeMaskingProvider implements MaskingProvider {
    private static final SWIFTCodeManager swiftCodeManager = SWIFTCodeManager.getInstance();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Swift code masking provider.
     */
    public SWIFTCodeMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param random the random
     */
    public SWIFTCodeMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public SWIFTCodeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Swift code masking provider.
     *
     * @param random               the random
     * @param maskingConfiguration the masking configuration
     */
    public SWIFTCodeMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.preserveCountry = maskingConfiguration.getBooleanValue("swift.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (this.preserveCountry) {
            return swiftCodeManager.getCodeFromCountry(identifier);
        }

        return swiftCodeManager.getRandomKey();
    }
}

