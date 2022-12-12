/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaceHierarchyTest {

    @Test
    public void testRaceHierarchy() {
        RaceHierarchy hierarchy = RaceHierarchy.getInstance();

        assertEquals(2, hierarchy.getHeight());
        assertEquals("Asian".toUpperCase(), hierarchy.encode("Asian", 0, false).toUpperCase());
        assertEquals("*".toUpperCase(), hierarchy.encode("Asian", 1, false).toUpperCase());
    }
}

