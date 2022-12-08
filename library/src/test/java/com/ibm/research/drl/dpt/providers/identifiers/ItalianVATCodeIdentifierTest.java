/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItalianVATCodeIdentifierTest {
    @Test
    public void testValid() {
        Identifier identifier = new ItalianVATCodeIdentifier();

        for (String valid : new String[]{
                "03449210123",
                "03967190962"
        }) {
            assertTrue(identifier.isOfThisType(valid));
        }
    }

    @Test
    public void testInvalid() {
        Identifier identifier = new ItalianVATCodeIdentifier();

        for (String invalid : new String[]{
                "0344921012",
                "0396719092AA",
                "BRGSFN81P10L682F"
        }) {
            assertFalse(identifier.isOfThisType(invalid));
        }
    }
}