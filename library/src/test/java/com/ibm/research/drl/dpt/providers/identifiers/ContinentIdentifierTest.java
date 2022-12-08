/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContinentIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        ContinentIdentifier identifier = new ContinentIdentifier();

        String[] validContinents = {
                "Asia",
                "Europe",
                "Africa",
                "europe"
        };

        for(String continent: validContinents) {
            assertTrue(identifier.isOfThisType(continent));
        }

        String[] invalidContinents = {
                "12344",
                "Eurape"
        };

        for(String continent: invalidContinents) {
            assertFalse(identifier.isOfThisType(continent));
        }
    }
}

