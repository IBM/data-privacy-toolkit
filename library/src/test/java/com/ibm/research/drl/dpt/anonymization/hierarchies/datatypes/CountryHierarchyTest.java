/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryHierarchyTest {
    @Test
    public void testCountryHierarchy() {
        CountryHierarchy countryHierarchy = CountryHierarchy.getInstance();

        assertEquals(3, countryHierarchy.getHeight());
        assertEquals("Italy".toUpperCase(), countryHierarchy.encode("Italy", 0, false).toUpperCase());
        assertEquals("Europe".toUpperCase(), countryHierarchy.encode("Italy", 1, false).toUpperCase());
        assertEquals("*", countryHierarchy.encode("Italy", 2, false));
    }
}
