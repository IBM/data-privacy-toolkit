/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 */
public class EmailIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        EmailIdentifier identifier = new EmailIdentifier();

        assertTrue(identifier.isOfThisType("john@ie.ibm.com"));
        assertTrue(identifier.isOfThisType("santonat@ie.ibm.com"));
        assertFalse(identifier.isOfThisType("something else"));
        assertFalse(identifier.isOfThisType("Help@IBM"));
    }

    @Test
    @Disabled
    public void testQuickCheckBenefit() {
        EmailIdentifier identifier = new EmailIdentifier(); 
        int runs = 1000000;

        String value = "this is definitely not a match";

        long start = System.currentTimeMillis();
        int matches = 0;
        for(int i = 0; i < runs; i++) {
            if (identifier.isOfThisType(value)) {
                matches++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("time : " + (end - start));
        System.out.println("matches: " + matches);

        start = System.currentTimeMillis();
        matches = 0;
        for(int i = 0; i < runs; i++) {
            if (quickCheck(value)) {
                if (identifier.isOfThisType(value)) {
                    matches++;
                }
            }
        }
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start));
        System.out.println("matches: " + matches);
    }

    private boolean quickCheck(String value) {
        return value.indexOf('@') != -1;
    }
}
