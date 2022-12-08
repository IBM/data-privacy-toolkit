/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.commons.validator.routines.IBANValidator;

import java.util.Arrays;
import java.util.Collection;

public class IBANIdentifier extends AbstractIdentifier {
    private final static IBANValidator ibanValidator = IBANValidator.getInstance();
    private final static String[] appropriateNames = new String[]{"IBAN"};

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.IBAN;
    }

    @Override
    public boolean isOfThisType(String data) {
        try {
            return ibanValidator.isValid(data);
        } catch (Exception ignored) {}

        return false;
    }

    @Override
    public String getDescription() {
        return "IBAN identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 8;
    }

    @Override
    public int getMaximumLength() {
        return 34;
    }
}
