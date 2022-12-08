/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonthIdentifierTest {

    @Test
    public void testOK() {

        String day  = "January";
        Identifier identifier = new MonthIdentifier();

        assertTrue(identifier.isOfThisType(day));
    }

    @Test
    public void testOKItalian() {

        String day  = "Gennaio";
        Identifier identifier = new MonthIdentifier();

        assertTrue(identifier.isOfThisType(day));
    }
}
