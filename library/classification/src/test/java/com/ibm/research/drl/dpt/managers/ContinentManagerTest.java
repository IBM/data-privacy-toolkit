/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2021                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
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
