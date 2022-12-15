/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShiftMaskingProviderTest {

    @Test
    public void testShiftValue() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("shift.mask.value", 10.0d);

        MaskingProvider maskingProvider = new ShiftMaskingProvider(maskingConfiguration);
        String originalValue = "15.34";

        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals(25.34d, Double.parseDouble(maskedValue), 0.0001);

    }

    @Test
    public void testShiftValueNegativeAndIntegerOffset() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("shift.mask.value", -10);

        MaskingProvider maskingProvider = new ShiftMaskingProvider(maskingConfiguration);
        String originalValue = "15.34";

        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals(5.34d, Double.parseDouble(maskedValue), 0.0001);

    }

    @Test
    @Disabled
    public void testPerformance() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("shift.mask.value", -10);

        MaskingProvider maskingProvider = new ShiftMaskingProvider(maskingConfiguration);
        
        long start = System.currentTimeMillis();

        String originalValue = "15.34";

        for(int i = 0; i < 100000; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertEquals(5.34d, Double.parseDouble(maskedValue), 0.0001);
        }
        
        long end = System.currentTimeMillis();

        System.out.println("time: " + (end - start));
    }
}

