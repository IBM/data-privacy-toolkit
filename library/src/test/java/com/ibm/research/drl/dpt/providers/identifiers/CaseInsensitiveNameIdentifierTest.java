/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaseInsensitiveNameIdentifierTest {
    @Test
    public void testPositive() throws Exception {
        CaseInsensitiveNameIdentifier identifier = new CaseInsensitiveNameIdentifier();

        assertTrue(identifier.isOfThisType("Carrol, John J."));
        assertTrue(identifier.isOfThisType("Carrol, John"));
        assertTrue(identifier.isOfThisType("Patrick K. Fitzgerald"));
        assertTrue(identifier.isOfThisType("Patrick Fitzgerald"));
        assertTrue(identifier.isOfThisType("Kennedy John"));

        assertTrue(identifier.isOfThisType("carrol, john j."));
        assertTrue(identifier.isOfThisType("carrol, john"));
        assertTrue(identifier.isOfThisType("patrick k. fitzgerald"));
        assertTrue(identifier.isOfThisType("patrick fitzgerald"));
        assertTrue(identifier.isOfThisType("kennedy john"));

        assertTrue(identifier.isOfThisType("giovanni"));

        assertTrue(identifier.isOfThisType("PaOLo"));

        assertTrue(identifier.isOfThisType("John E. Kelly"));
    }

    @Test
    public void testNegative() throws Exception {
        CaseInsensitiveNameIdentifier identifier = new CaseInsensitiveNameIdentifier();

        assertFalse(identifier.isOfThisType("12308u499234802"));
        assertFalse(identifier.isOfThisType("84032-43092-3242"));

        assertFalse(identifier.isOfThisType("32 Carter Avn."));
        assertFalse(identifier.isOfThisType("15 Kennedy Avenue"));
        assertFalse(identifier.isOfThisType("Thompson Avn 1000"));
        assertFalse(identifier.isOfThisType("10 20 33"));

        assertFalse(identifier.isOfThisType("il mio"));
    }
}