/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeightHierarchyTest {
    
    @Test
    public void testEncoding() {
        HeightHierarchy hierarchy = HeightHierarchy.getInstance();
        
        String originalValue = "156.23";
        String originalValue2 = "157.23";
        
        assertEquals(originalValue, hierarchy.encode(originalValue, 0, true));
        assertEquals("*", hierarchy.encode(originalValue, 20, true));
        assertEquals("156-157", hierarchy.encode(originalValue, 1, true));
        assertEquals("156-158", hierarchy.encode(originalValue, 2, true));
        assertEquals("156-160", hierarchy.encode(originalValue, 3, true));

        assertEquals("157-158", hierarchy.encode(originalValue2, 1, true));
        assertEquals("156-158", hierarchy.encode(originalValue2, 2, true));
        assertEquals("156-160", hierarchy.encode(originalValue2, 3, true));

    }
    
    @Test
    public void testLevels() {
        HeightHierarchy hierarchy = HeightHierarchy.getInstance();

        String originalValue = "156.23";
       
        assertEquals(0, hierarchy.getNodeLevel(originalValue));
        assertEquals(1, hierarchy.getNodeLevel("158-159"));
        assertEquals(2, hierarchy.getNodeLevel("158-160"));
        assertEquals(3, hierarchy.getNodeLevel("158-162"));
        assertEquals(4, hierarchy.getNodeLevel("158-166"));
        assertEquals(5, hierarchy.getNodeLevel("158-168"));
        assertEquals(6, hierarchy.getNodeLevel("158-178"));
        assertEquals(7, hierarchy.getNodeLevel("158-188"));
        assertEquals(hierarchy.getHeight() - 1, hierarchy.getNodeLevel(hierarchy.getTopTerm()));
    }
}

