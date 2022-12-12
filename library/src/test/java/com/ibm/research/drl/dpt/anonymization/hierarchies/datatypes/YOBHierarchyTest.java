/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YOBHierarchyTest {

    @Test
    public void testYOBHierarchy() {
        YOBHierarchy hierarchy = YOBHierarchy.getInstance();

        assertEquals(5, hierarchy.getHeight());
        assertEquals("1950", hierarchy.encode("1950", 0, false));
        assertEquals("1950-1951", hierarchy.encode("1950", 1, false));
        assertEquals("1948-1951", hierarchy.encode("1950", 2, false));
        assertEquals("1944-1951", hierarchy.encode("1950", 3, false));
        assertEquals("*", hierarchy.encode("1950", 4, false));

        assertEquals("1951", hierarchy.encode("1951", 0, false));
        assertEquals("1950-1951", hierarchy.encode("1951", 1, false));
        assertEquals("1948-1951", hierarchy.encode("1951", 2, false));
        assertEquals("1944-1951", hierarchy.encode("1951", 3, false));
        assertEquals("*", hierarchy.encode("1951", 4, false));
    }
    
    @Test
    public void testLeaves() {
        YOBHierarchy hierarchy = YOBHierarchy.getInstance();
        assertEquals(0, hierarchy.getNodeLeaves("1978").size());
        assertEquals(2, hierarchy.getNodeLeaves("1978-1979").size());
    }
}
