/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreetNameManagerTest {

    @Test
    public void testLookupSuccessful() throws Exception {
        StreetNameManager streetNameManager = StreetNameManager.getInstance();

        String streetName = "Woodland";
        assertTrue(streetNameManager.isValidKey(streetName));

        //case checking
        streetName = "WooDLand";
        assertTrue(streetNameManager.isValidKey(streetName));
    }

    @Test
    public void testRandomKeySuccessful() throws Exception {
        StreetNameManager streetNameManager = StreetNameManager.getInstance();

        String streetName = "Woodland";
        String randomStreetName = streetNameManager.getRandomKey();

        assertFalse(randomStreetName.equals(streetName));
    }

}
