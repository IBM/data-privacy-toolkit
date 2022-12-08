/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimalSpeciesIdentifierTest {

    @Test
    public void testIsOfThisType() {
        AnimalSpeciesIdentifier identifier = new AnimalSpeciesIdentifier();

        String value = "Dog";

        assertTrue(identifier.isOfThisType(value));
    }
}
