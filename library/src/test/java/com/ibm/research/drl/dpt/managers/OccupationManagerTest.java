/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OccupationManagerTest {

    @Test
    public void testLookup() {
        OccupationManager occupationManager = OccupationManager.getInstance();

        String occupation = "actor";
        assertTrue(occupationManager.isValidKey(occupation));
        assertTrue(occupationManager.isValidKey(occupation.toLowerCase()));
        assertTrue(occupationManager.isValidKey(occupation.toUpperCase()));
        occupation = "acTor";
        assertTrue(occupationManager.isValidKey(occupation));

        occupation = "official";
        assertFalse(occupationManager.isValidKey(occupation));
    }

    @Test
    public void testFalsePositives() {
        String[] values = {
               "C",
               "Z",
               "S",
               "P",
               "N",
               "G",
               "O",
               "-"
        };

        OccupationManager occupationManager = OccupationManager.getInstance();
        for(String value: values) {
            assertThat(occupationManager.isValidKey(value), is(false));
        }

    }
}
