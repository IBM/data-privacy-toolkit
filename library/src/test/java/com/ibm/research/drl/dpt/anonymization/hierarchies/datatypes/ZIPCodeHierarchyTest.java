/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZIPCodeHierarchyTest {
    
    @Test
    public void testLeaves() {
        ZIPCodeHierarchy hierarchy = new ZIPCodeHierarchy();

        assertEquals(10, hierarchy.getNodeLeaves("1234*").size());
        
        
        assertEquals(100, hierarchy.getNodeLeaves("123**").size());
    }
}

