/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaritalStatusHierarchyTest {

    @Test
    public void testMaritalStatusHierarchy() {
        MaritalStatusHierarchy hierarchy = MaritalStatusHierarchy.getInstance();

        assertEquals(3, hierarchy.getHeight());
        assertEquals("Single".toUpperCase(), hierarchy.encode("Single", 0, false).toUpperCase());
        assertEquals("Alone".toUpperCase(), hierarchy.encode("Single", 1, false).toUpperCase());
        assertEquals("*".toUpperCase(), hierarchy.encode("Single", 2, false).toUpperCase());
    }
}

