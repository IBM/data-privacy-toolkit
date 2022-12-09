/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IBANIdentifier;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.security.SecureRandom;

public class IBANMaskingProvider extends AbstractMaskingProvider {
    private static final IBANIdentifier ibanIdentifier = new IBANIdentifier();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Iban masking provider.
     */
    public IBANMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param random the random
     */
    public IBANMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param configuration the configuration
     */
    public IBANMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IBANMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveCountry = configuration.getBooleanValue("iban.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (ibanIdentifier.isOfThisType(identifier) && this.preserveCountry) {
            return Iban.random(CountryCode.valueOf(identifier.substring(0, 2))).toString();
        }

        return Iban.random().toString();
    }
}
