/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSNUKIdentifierTest {

    @Test
    public void testIsOfThisType() {
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";
        assertTrue(identifier.isOfThisType(ssn));

        //ignores spaces
        ssn = "AB 12 34 56 C";
        assertTrue(identifier.isOfThisType(ssn));

        //check for not allowed characters
        ssn = "DB123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "AD123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "AO123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA12A456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA1234567";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA123456Z";
        assertFalse(identifier.isOfThisType(ssn));

        //'O' is allowed on the first character
        ssn = "OA123456C";
        assertTrue(identifier.isOfThisType(ssn));
    }
}
