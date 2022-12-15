/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

public class NumericIdentifier extends AbstractIdentifier {
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.NUMERIC;
    }

    @Override
    public boolean isOfThisType(String data) {
        int digits = 0;

        for (int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                digits++;
            }
        }

        if (digits == 0) {
            return false;
        }

        try {
            Double d = Double.parseDouble(data);
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "Numeric description";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
