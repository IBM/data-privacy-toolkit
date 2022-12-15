/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ICDv9HierarchyTest {

    @Test
    public void testRaceHierarchy() {
        ICDv9Hierarchy hierarchy = ICDv9Hierarchy.getInstance();

        assertEquals(4, hierarchy.getHeight());
        assertEquals("250.61".toUpperCase(), hierarchy.encode("250.61", 0, false).toUpperCase());
        assertEquals("250".toUpperCase(), hierarchy.encode("250.61", 1, false).toUpperCase());
        assertEquals("240-279".toUpperCase(), hierarchy.encode("250.61", 2, false).toUpperCase());
        assertEquals("*".toUpperCase(), hierarchy.encode("250.61", 3, false).toUpperCase());
    }
}

