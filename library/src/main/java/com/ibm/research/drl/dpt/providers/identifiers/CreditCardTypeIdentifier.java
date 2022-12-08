/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.CreditCardTypeManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class CreditCardTypeIdentifier extends AbstractManagerBasedIdentifier {

    private static final String[] appropriateNames = {"Credit Card Type"};
    private static final CreditCardTypeManager creditCardTypeManager = CreditCardTypeManager.getInstance();

    @Override
    protected Manager getManager() {
        return creditCardTypeManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.CREDIT_CARD_TYPE;
    }

    @Override
    public String getDescription() {
        return "Credit Card Type identification";
    }

    @Override
    public Collection<ProviderType> getLinkedTypes() {
        return Arrays.asList(ProviderType.CREDIT_CARD);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return creditCardTypeManager.getMinimumLength();
    }

    @Override
    public int getMaximumLength() {
        return creditCardTypeManager.getMaximumLength();
    }

}
