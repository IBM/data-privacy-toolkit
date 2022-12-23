/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VINManagerTest {

    @Test
    public void testLookupSuccessful() {
        VINManager vinManager = new VINManager();
        String wmi = "1C3";
        assertTrue(vinManager.isValidWMI(wmi));

        //check that the lowercase version is also matched
        wmi = "1c3";
        assertTrue(vinManager.isValidWMI(wmi));
    }

    @Test
    public void testRandomWMIGenerator() {
        VINManager vinManager = new VINManager();
        //test random WMI

        for(int i = 0; i < 1000; i++) {
            assertTrue(vinManager.isValidWMI(vinManager.getRandomWMI()));
        }

        String exceptionWMI = "1C3";
        for(int i = 0; i < 1000; i++) {
            String randomWmi = vinManager.getRandomWMI(exceptionWMI);
            assertNotEquals(randomWmi, exceptionWMI);
        }
    }
}
