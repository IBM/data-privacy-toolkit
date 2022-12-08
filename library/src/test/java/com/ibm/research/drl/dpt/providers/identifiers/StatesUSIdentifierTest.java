/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatesUSIdentifierTest {

    @Test
    public void testIdentification() {
        StatesUSIdentifier identifier = new StatesUSIdentifier();
        String value = "Alabama";
        assertTrue(identifier.isOfThisType(value));

        value = "AL";
        assertTrue(identifier.isOfThisType(value));
    }

    @Test
    public void testIdentificationIgnoresCase() {
        StatesUSIdentifier identifier = new StatesUSIdentifier();
        String value = "aLABama";
        assertTrue(identifier.isOfThisType(value));
    }
}
