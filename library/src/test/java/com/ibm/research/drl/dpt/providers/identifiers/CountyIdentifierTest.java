/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountyIdentifierTest {

    @Test
    public void testFullName() {
        Identifier identifier = new CountyIdentifier();

        String originalValue = "Pendleton County";
        assertTrue(identifier.isOfThisType(originalValue));
    }

    @Test
    public void testShortName() {
        Identifier identifier = new CountyIdentifier();

        String originalValue = "Pendleton";
        assertTrue(identifier.isOfThisType(originalValue));
    }
}
