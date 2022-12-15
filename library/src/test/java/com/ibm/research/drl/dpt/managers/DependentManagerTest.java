/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

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
