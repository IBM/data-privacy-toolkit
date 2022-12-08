/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.SWIFTCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SWIFTCodeManagerTest {

    @Test
    public void testLookup() {
        SWIFTCodeManager swiftCodeManager = SWIFTCodeManager.getInstance();

        String key = "EMCRGRA1";
        assertTrue(swiftCodeManager.isValidKey(key));

        SWIFTCode code = swiftCodeManager.getKey(key);
        assertTrue(code.getCode().equals(key));
        assertTrue(code.getCountry().getName().toUpperCase().equals("GREECE"));
    }
}
