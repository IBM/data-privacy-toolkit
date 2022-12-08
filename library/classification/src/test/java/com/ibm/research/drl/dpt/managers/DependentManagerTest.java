package com.ibm.research.drl.dpt.managers;
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependentManagerTest {

    @Test
    public void testDependentManager() throws Exception {
        DependentManager dependentManager = DependentManager.getInstance();

        String value = "daughter";
        assertTrue(dependentManager.isValidKey(value));

        //case checking
        value = "dAUghTer";
        assertTrue(dependentManager.isValidKey(value));
    }
}
