/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DayIdentifierTest {

    @Test
    public void testOK() {

        String day  = "Monday";
        Identifier identifier = new DayIdentifier();

        assertTrue(identifier.isOfThisType(day));
    }
}
