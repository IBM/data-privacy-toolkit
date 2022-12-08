/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZIPCodeManagerTest {

    @Test
    public void test() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);
        assertTrue(zipCodeManager.isValidKey("US", "00601"));
    }

    @Test
    public void testPopulation() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        Integer population = zipCodeManager.getPopulation("US", "00601");
        assertNotNull(population);
        assertEquals(18570, population.intValue());
    }

    @Test
    public void testPopulationThreeDigitPrefix() {

        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        Integer population = zipCodeManager.getPopulationByPrefix("US", "00601");
        assertNotNull(population);
        assertEquals(1214568, population.intValue());
    }

    @Test
    public void testInvalidCountry() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        Integer population = zipCodeManager.getPopulationByPrefix("#$@", "00601");
        assertEquals(0, population.intValue());

        population = zipCodeManager.getPopulation("#$@", "00601");
        assertEquals(0, population.intValue());
    }

    @Test
    public void testInvalidCode() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        Integer population = zipCodeManager.getPopulationByPrefix("US", "!@$@%%");
        assertEquals(0, population.intValue());

        population = zipCodeManager.getPopulation("US", "!#!#!@#!#!");
        assertEquals(0, population.intValue());
    }
}
