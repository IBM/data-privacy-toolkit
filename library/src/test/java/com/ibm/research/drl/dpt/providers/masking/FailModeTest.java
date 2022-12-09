/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FailModeTest {
    
    @Test
    public void testFailModeOnNumericMaskingProvidersReturnEmpty() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_EMPTY);
        
        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };
        
        String invalidValue = "abc";
        
        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            assertEquals("", maskingProvider.mask(invalidValue), maskingProvider.getClass().getName());
        }
        
    }

    @Test
    public void testFailModeOnNumericMaskingProvidersReturnOriginal() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_ORIGINAL);

        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };

        String invalidValue = "abc";

        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            assertEquals(invalidValue, maskingProvider.mask(invalidValue), maskingProvider.getClass().getName());
        }
    }

    @Test
    public void testFailModeOnNumericMaskingProvidersThrowError() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.THROW_ERROR);

        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };

        String invalidValue = "abc";

        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            try {
                String mask = maskingProvider.mask(invalidValue);
            } catch (RuntimeException e) {
                continue;
            }
            
            assertFalse(true, "should fail");
        }
    }
}

