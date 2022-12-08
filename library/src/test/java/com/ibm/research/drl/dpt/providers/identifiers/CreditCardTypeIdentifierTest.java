/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreditCardTypeIdentifierTest {

    @Test
    public void testIsOfThisType() {
        CreditCardTypeIdentifier identifier = new CreditCardTypeIdentifier();

        String originalValue = "VISA";
        assertTrue(identifier.isOfThisType(originalValue));
        originalValue = "vISa";
        assertTrue(identifier.isOfThisType(originalValue));
    }
}
