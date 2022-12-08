/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContinentManagerTest {

    @Test
    public void testLocalization() {
        //this test assumes that GR is loaded by default
        ContinentManager continentManager = ContinentManager.getInstance();

        String english = "Europe";
        assertTrue(continentManager.isValidKey(english));

        String greek = "Ευρώπη";
        assertTrue(continentManager.isValidKey(greek));
    }
}
