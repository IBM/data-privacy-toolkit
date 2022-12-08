/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReligionManagerTest {

    @Test
    public void testLookupSuccessful() throws Exception {
        ReligionManager religionManager = ReligionManager.getInstance();
        String religion = "Catholic";
        assertTrue(religionManager.isValidKey(religion));

        religion = "caTHolic";
        assertTrue(religionManager.isValidKey(religion));
    }

    @Test
    public void testRandomCodeGenerator() throws Exception {
        ReligionManager religionManager = ReligionManager.getInstance();
        assertTrue(religionManager.isValidKey(religionManager.getRandomKey()));
    }

}
