/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider maskingProvider = new BooleanMaskingProvider();
        String value = "true";

        int randomOK = 0;
        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            if (!maskedValue.equals(value)) {
                randomOK++;
            }
        }

        assertTrue(randomOK > 0);
    }
}

