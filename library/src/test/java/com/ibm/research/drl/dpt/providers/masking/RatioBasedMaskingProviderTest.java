/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RatioBasedMaskingProviderTest {
    
    
    @Test
    public void testMask() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("ratiobased.mask.ratio", 2.0);

        MaskingProvider maskingProvider = new RatioBasedMaskingProvider(maskingConfiguration);
        
        String input = "2.5";
        String masked = maskingProvider.mask(input);
        
        assertEquals(5.0, Double.parseDouble(masked), 0.00000000001);
        
    }

    @Test
    public void testMaskCompound() {
        MaskingProvider maskingProvider = new RatioBasedMaskingProvider();
        
        String identifier = "5.0";
        String fieldName = "Euro";
        
        String masked = maskingProvider.maskWithRatio(identifier, "12.0", "8.0");
        //the original ratio was 8/5 = 1.6
        //the masked value for Euro must be 12/1.6 = 7.5
        
        assertEquals(7.5, Double.parseDouble(masked), 0.000001);
    }

    @Test
    public void testMaskCompoundEmptyOperand() {
        String identifier = "5.0";
        String fieldName = "Euro";

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_EMPTY);

        RatioBasedMaskingProvider maskingProvider = new RatioBasedMaskingProvider(maskingConfiguration);
        String masked = maskingProvider.maskWithRatio(identifier, "", "");

        assertEquals("", masked);
    }

    @Test
    public void testMaskCompoundGreater() {
        RatioBasedMaskingProvider maskingProvider = new RatioBasedMaskingProvider();

        String identifier = "15.0";
        String fieldName = "Euro";

        String masked = maskingProvider.maskWithRatio(identifier, "12.0", "5.0");
        //the original ratio was 5/15 = 0.33333
        //the masked value for Euro must be 12/0.3333 = 36 

        assertEquals(36.0, Double.parseDouble(masked), 0.000001);
    }

    @Test
    public void testMaskWithRatioAsOperand() {
        RatioBasedMaskingProvider maskingProvider = new RatioBasedMaskingProvider();

        String identifier = "15.0";

        String masked = maskingProvider.maskWithKey(identifier, "3.0");

        //Rate holds the ratio, so masked result = 15.0 * 3
        assertEquals(15.0 * 3.0, Double.parseDouble(masked), 0.000001);
    }
}
