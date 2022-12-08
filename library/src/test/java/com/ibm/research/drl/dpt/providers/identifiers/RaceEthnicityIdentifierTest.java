/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RaceEthnicityIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        RaceEthnicityIdentifier identifier = new RaceEthnicityIdentifier();

        String race = "White";
        assertTrue(identifier.isOfThisType(race));

        //ignores case
        assertTrue(identifier.isOfThisType("white"));
    }
}
