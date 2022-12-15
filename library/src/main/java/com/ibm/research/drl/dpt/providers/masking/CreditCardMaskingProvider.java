/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

/**
 * The type Credit card masking provider.
 */
public class CreditCardMaskingProvider extends AbstractMaskingProvider {
    private final boolean preserveIssuer;
    private int preservedDigits = 0;

    /**
     * Instantiates a new Credit card masking provider.
     */
    public CreditCardMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param configuration the configuration
     */
    public CreditCardMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param random the random
     */
    public CreditCardMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Credit card masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CreditCardMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;

        this.preserveIssuer = configuration.getBooleanValue("creditCard.issuer.preserve");
        if (preserveIssuer) {
            this.preservedDigits = 6;
        }
    }

    @Override
    public String mask(String identifier) {
        if (!preserveIssuer) {
            return RandomGenerators.generateRandomCreditCard();
        }

        final StringBuilder buffer = new StringBuilder();

        int digitsEncountered = 0;
        int identifierLength = identifier.length();
        for (int i = 0; i < identifierLength; i++) {
            char c = identifier.charAt(i);

            if (Character.isDigit(c)) {
                digitsEncountered++;

                if (digitsEncountered > preservedDigits) {
                    if (i < (identifierLength - 1)) {
                        c = RandomGenerators.randomDigit();
                    } else {
                        c = (char) ('0' + RandomGenerators.luhnCheckDigit(buffer.toString().replaceAll("[\\- ]", "")));
                    }
                }
            }

            buffer.append(c);
        }

        return buffer.toString();
    }

}
