/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenderIdentifierTest {

    @Test
    public void testMatch() {
        Identifier identifier = new GenderIdentifier();

        String value = "Male";
        assertTrue(identifier.isOfThisType(value));
    }

    @Test
    public void testMatchIgnoresCase() {
        Identifier identifier = new GenderIdentifier();

        String value = "MaLE";
        assertTrue(identifier.isOfThisType(value));
    }
}
