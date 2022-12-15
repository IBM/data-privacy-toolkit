/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class NumberVarianceMaskingProviderTest {

    @Test
    @Disabled
    public void testWithLimits() {
        double margin = 15.0;
        
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("numvariance.mask.limitDown", margin);
        configuration.setValue("numvariance.mask.limitUp", margin);
        
        NumberVarianceMaskingProvider maskingProvider = new NumberVarianceMaskingProvider(configuration);
        
        String value = "10.0";
        for(int i = 0; i < 100; i++) {
            System.out.println(maskingProvider.mask(value));
        }
                
                
    }
    
    @Test
    public void testMask() {
        
        double margin = 15.0;
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("numvariance.mask.limitDown", margin);
        configuration.setValue("numvariance.mask.limitUp", margin);
        MaskingProvider maskingProvider = new NumberVarianceMaskingProvider(configuration);

        double originalValue = 50d;
        String value = originalValue.toString();

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            Double maskedDouble = Double.valueOf(maskedValue);
            assertTrue(maskedDouble >= (originalValue - margin));
            assertTrue(maskedDouble <= (originalValue + margin));
        }
    }

    @Test
    public void testMaskWrongMargin() {
        assertThrows(RuntimeException.class, () -> {
            double margin = -15.0;

            MaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("numvariance.mask.limitDown", margin);
            configuration.setValue("numvariance.mask.limitUp", margin);
            MaskingProvider maskingProvider = new NumberVarianceMaskingProvider(configuration);

            double originalValue = 50.456788;
            String value = originalValue.toString();

            for (int i = 0; i < 100; i++) {
                String maskedValue = maskingProvider.mask(value);

                assertThat(maskedValue, is(not(value)));
            }
        });
    }
    
    @Test
    public void testMaskPrecisionDigitsWrongValue() {
        assertThrows(RuntimeException.class, () -> {
            double margin = 15.0;
            int precisionDigits = -3;

            MaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("numvariance.mask.limitDown", margin);
            configuration.setValue("numvariance.mask.limitUp", margin);
            configuration.setValue("numvariance.mask.precisionDigits", precisionDigits);
            MaskingProvider maskingProvider = new NumberVarianceMaskingProvider(configuration);

            Double originalValue = 50.456788;
            String value = originalValue.toString();

            for (int i = 0; i < 100; i++) {
                String maskedValue = maskingProvider.mask(value);
                String[] parts = maskedValue.split("\\.");

                assertThat(parts[1].length(), is(lessThanOrEqualTo(precisionDigits)));
            }
        });
    }
    
    @Test
    public void testMaskPrecisionDigits() {

        double margin = 15.0;
        int precisionDigits = 3;
        
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("numvariance.mask.limitDown", margin);
        configuration.setValue("numvariance.mask.limitUp", margin);
        configuration.setValue("numvariance.mask.precisionDigits", precisionDigits);
        MaskingProvider maskingProvider = new NumberVarianceMaskingProvider(configuration);

        Double originalValue = 50.456788;
        String value = originalValue.toString();

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            String[] parts = maskedValue.split("\\.");
            assertTrue(parts[1].length() <= precisionDigits);
        }
    }
}

