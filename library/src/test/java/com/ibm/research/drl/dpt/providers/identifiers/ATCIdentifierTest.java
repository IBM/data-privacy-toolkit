/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ATCIdentifierTest {

    @Test
    public void testIsOfThisType() {
        Identifier identifier = new ATCIdentifier();

        String atc = "A04AA02";
        assertTrue(identifier.isOfThisType(atc));
        atc = "a02aa01";
        assertTrue(identifier.isOfThisType(atc));
    }
}
