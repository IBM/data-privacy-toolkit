/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;

/**
 * The type National identifier masking provider.
 */
public class NationalIdentifierMaskingProvider implements MaskingProvider {
    private final SecureRandom random;

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param random the random
     */
    public NationalIdentifierMaskingProvider(SecureRandom random) {
        this.random = random;
    }

    /**
     * Instantiates a new National identifier masking provider.
     */
    public NationalIdentifierMaskingProvider() {
        this(new SecureRandom());
    }

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param configuration the configuration
     */
    public NationalIdentifierMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom());
    }

    /**
     * Instantiates a new National identifier masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public NationalIdentifierMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this(random);
    }

    @Override
    public String mask(String identifier) {
        StringBuilder builder = new StringBuilder(identifier.length());
        int identifierLength = identifier.length();

        for (int i = 0; i < identifierLength; ++i) {
            char c = identifier.charAt(i);

            if (Character.isDigit(c)) {
                c = (char) ('0' + random.nextInt(9));
            } else if (Character.isLetter(c)) {
                c = (char) ((Character.isLowerCase(c) ? 'a' : 'A') + random.nextInt(26));
            }

            builder.append(c);
        }

        return builder.toString();
    }
}
