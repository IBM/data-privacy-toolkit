/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreetTypeManagerTest {

    @Test
    public void testStreetTypeManager() throws Exception {
        StreetTypeManager streetNameManager = StreetTypeManager.getInstance();

        String streetName = "Street";
        assertTrue(streetNameManager.isValidKey(streetName));

        //case checking
        streetName = "stREEt";
        assertTrue(streetNameManager.isValidKey(streetName));
    }
}
