/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Calendar;

public class YOBIdentifier extends AbstractIdentifier {
    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.YOB;
    }

    @Override
    public boolean isOfThisType(String data) {
        int digits = 0;

        for(int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                digits++;
            }
        }

        if (digits == 0) {
            return false;
        }
        
        try {
            int yob = Integer.parseInt(data);

            return yob >= (currentYear - 100) && yob <= currentYear;
        } catch (Exception ignored) {}

        return false;
    }

    @Override
    public String getDescription() {
        return "Year of birth";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 4;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
