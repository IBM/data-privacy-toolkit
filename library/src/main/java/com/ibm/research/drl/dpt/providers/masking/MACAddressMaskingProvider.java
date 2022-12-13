/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.MACAddressIdentifier;

import java.security.SecureRandom;

public class MACAddressMaskingProvider extends AbstractMaskingProvider {
    private static final char[] allowedCharacters = "abcdef0123456789".toCharArray();
    private static final MACAddressIdentifier macAddressIdentifier = new MACAddressIdentifier();
    private final boolean preserveVendor;

    /**
     * Instantiates a new Mac address masking provider.
     */
    public MACAddressMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param configuration the configuration
     */
    public MACAddressMaskingProvider(DefaultMaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param random the random
     */
    public MACAddressMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Mac address masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public MACAddressMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveVendor = configuration.getBooleanValue("mac.masking.preserveVendor");
    }

    private String randomMACAddress(int octets) {
        int subsetLength = allowedCharacters.length;

        StringBuilder builder = new StringBuilder((octets - 1) * 3 + 2);
        for (int i = 0; i < (octets - 1); i++) {
            builder.append(allowedCharacters[random.nextInt(subsetLength)]);
            builder.append(allowedCharacters[random.nextInt(subsetLength)]);
            builder.append(':');
        }

        builder.append(allowedCharacters[random.nextInt(subsetLength)]);
        builder.append(allowedCharacters[random.nextInt(subsetLength)]);

        return builder.toString();
    }

    @Override
    public String mask(String identifier) {
        if (!preserveVendor) {
            return randomMACAddress(6);
        }

        if (!macAddressIdentifier.isOfThisType(identifier)) {
            return randomMACAddress(6);
        }

        return identifier.substring(0, 9) + randomMACAddress(3);
    }
}

