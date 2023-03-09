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

