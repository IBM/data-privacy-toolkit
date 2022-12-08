/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternationalPhoneIdentifierTest {

    @Test
    public void testIsOfThisType() {
        InternationalPhoneIdentifier identifier = new InternationalPhoneIdentifier();

        String[] validNumbers = {
                "+353-0876653255",
                "+353 0876653255",
                "+353 087 665 3255",
                "+353 087 665  3255",
                "00353-0876653255",
                "+353-(087)6653255",
                "0044-(087)6653255",
                "0044 (087)6653255",
                "0044 0876653255",
                "+622125669098"
        };

        for (int i = 0; i < validNumbers.length; ++i) {
            String number = validNumbers[i];
            assertTrue(identifier.isOfThisType(number), number);
        }

        String[] invalidNumbers = {
                "+353-0876653255aaaa",
                "+353-0876653255123123123123131231331131212121",
                "+353-(0876653255",
                "622125669098",
                "+622125669098a",
                "+999125669098",
                "+62123"
        };

        for(String invalidNumber: invalidNumbers) {
            assertFalse(identifier.isOfThisType(invalidNumber), invalidNumber);
        }
    }

    @Test
    @Disabled
    public void testPhoneList() throws Exception {
        InternationalPhoneIdentifier identifier = new InternationalPhoneIdentifier();

        try(BufferedReader br = new BufferedReader(new FileReader("/Users/santonat/dev/organizations/international_phone_numbers.csv"))) {
            for (String line; (line = br.readLine()) != null; ) {
                String number = line.trim();
                if(!identifier.isOfThisType(number)) {
                    System.out.println(number);
                }
            }
        }
    }
}
