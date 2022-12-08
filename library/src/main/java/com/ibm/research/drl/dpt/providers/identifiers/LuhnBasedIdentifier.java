/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

public interface LuhnBasedIdentifier {
    default boolean checkLastDigit(String value) {
        int sum = 0;
        boolean doubleValue = false;

        for (int i = value.length() - 1; i >= 0; --i) {
            int currentDigit = value.charAt(i) - '0';

            if (doubleValue) {
                currentDigit *= 2;

                if (currentDigit > 9) {
                    currentDigit -= 9;
                }
            }

            sum += currentDigit;

            doubleValue = !doubleValue;
        }

        return 0 == sum % 10;
    }
}
