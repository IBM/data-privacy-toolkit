/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CreditCardManager;
import com.ibm.research.drl.dpt.managers.CreditCardTypeManager;
import com.ibm.research.drl.dpt.models.CreditCard;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.security.SecureRandom;
import java.util.Map;

public class CreditCardTypeMaskingProvider extends AbstractMaskingProvider {
    private static final CreditCardTypeManager ccTypeManager = CreditCardTypeManager.getInstance();
    private static final CreditCardManager creditCardManager = CreditCardManager.getInstance();

    /**
     * Instantiates a new Credit card type masking provider.
     */
    public CreditCardTypeMaskingProvider() {

    }

    /**
     * Instantiates a new Credit card type masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CreditCardTypeMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {

    }

    @Override
    public String maskLinked(String identifier, String maskedValue, ProviderType providerType) {
        CreditCard creditCard = creditCardManager.lookupInfo(maskedValue);

        if (creditCard == null) {
            return mask(identifier);
        }

        return creditCard.getName();
    }

    @Override
    public String mask(String identifier) {
        return ccTypeManager.getRandomKey();
    }
}

