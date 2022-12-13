/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class SSNUKIdentifier extends AbstractIdentifier {
    /**
     * The Appropriate names.
     */
    final static String[] appropriateNames = {"SSN"};

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SSN_UK;
    }

    @Override
    public boolean isOfThisType(String data) {
        String ssn = data.replace(" ", "").toUpperCase();
        if (ssn.length() != 9) {
            return false;
        }

        char first = ssn.charAt(0);
        char second = ssn.charAt(1);
        if (!Character.isAlphabetic(first) || !Character.isAlphabetic(second)) {
            return false;
        }

        if (first == 'D' || first == 'F' || first == 'I' || first == 'Q' || first == 'U' || first == 'V') {
            return false;
        }

        if (second == 'D' || second == 'F' || second == 'I' || second == 'Q'
                || second == 'U' || second == 'V' || second == 'O') {
            return false;
        }

        for (int i = 2; i < 8; i++) {
            if (!Character.isDigit(ssn.charAt(i))) {
                return false;
            }
        }

        char last = ssn.charAt(8);
        return last >= 'A' && last <= 'D';
    }

    @Override
    public String getDescription() {
        return "SSN identification for UK";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 9;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
