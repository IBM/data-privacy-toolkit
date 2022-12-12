/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReligionHierarchyTest {
    @Test
    public void correctness() throws Exception {
        ReligionHierarchy hierarchy = ReligionHierarchy.getInstance();

        assertEquals(3, hierarchy.getHeight());
        assertEquals("Roman Catholic".toUpperCase(), hierarchy.encode("Roman Catholic", 0, false).toUpperCase());
        assertEquals("Christian".toUpperCase(), hierarchy.encode("Roman Catholic", 1, false).toUpperCase());
        assertEquals("*".toUpperCase(), hierarchy.encode("Roman Catholic", 2, false).toUpperCase());
    }
}