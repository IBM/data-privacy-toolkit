/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaritalStatusManagerTest {

    @Test
    public void testLookupSuccessful() throws Exception {
        MaritalStatusManager maritalStatusManager = MaritalStatusManager.getInstance();
        String status = "Single";
        assertTrue(maritalStatusManager.isValidKey(status));

        status = "singLE";
        assertTrue(maritalStatusManager.isValidKey(status));
    }

    @Test
    public void testRandomCodeGenerator() throws Exception {
        MaritalStatusManager maritalStatusManager = MaritalStatusManager.getInstance();
        assertTrue(maritalStatusManager.isValidKey(maritalStatusManager.getRandomKey()));
    }

}
