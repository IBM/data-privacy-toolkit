/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityHierarchyTest {

    @Test
    public void testCityHierarchy() {

        CityHierarchy cityHierarchy = CityHierarchy.getInstance();

        assertEquals(4, cityHierarchy.getHeight());
        assertEquals("Rome".toUpperCase(), cityHierarchy.encode("Rome", 0, false).toUpperCase());
        assertEquals("Italy".toUpperCase(), cityHierarchy.encode("Rome", 1, false).toUpperCase());
        assertEquals("Europe".toUpperCase(), cityHierarchy.encode("Rome", 2, false).toUpperCase());
        assertEquals("*", cityHierarchy.encode("Rome", 3, false));

        
    }
}

