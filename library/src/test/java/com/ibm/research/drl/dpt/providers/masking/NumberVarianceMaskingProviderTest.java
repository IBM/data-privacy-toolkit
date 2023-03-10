/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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
        String value = Double.toString(originalValue);

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            double maskedDouble = Double.parseDouble(maskedValue);
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
            String value = Double.toString(originalValue);

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

            double originalValue = 50.456788;
            String value = Double.toString(originalValue);

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

        double originalValue = 50.456788;
        String value = Double.toString(originalValue);

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            String[] parts = maskedValue.split("\\.");
            assertTrue(parts[1].length() <= precisionDigits);
        }
    }
}

