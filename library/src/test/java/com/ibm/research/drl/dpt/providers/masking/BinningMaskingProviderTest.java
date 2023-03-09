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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BinningMaskingProviderTest {

    @Test
    public void testDefault() {
        MaskingProvider maskingProvider = new BinningMaskingProvider();

        String originalValue = "22";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("20-25", maskedValue);
    }

    @Test
    @Disabled
    public void testPerformance() {
        MaskingProvider maskingProvider = new BinningMaskingProvider();

        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            String originalValue = "22.5";
            String maskedValue = maskingProvider.mask(originalValue);
            assertEquals("20-25", maskedValue);
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
    
    @Test
    public void testBinSize() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("binning.mask.binSize", 10);
        MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

        String originalValue = "22";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("20-30", maskedValue);
    }

    @Test
    public void testInvalidBinSize() {
        assertThrows(RuntimeException.class, () -> {
            MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
            maskingConfiguration.setValue("binning.mask.binSize", -10);
            MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

            String originalValue = "22";
            String maskedValue = maskingProvider.mask(originalValue);

            assertEquals("20-30", maskedValue);
        });
    }

    @Test
    public void testBinSizeZero() {
        assertThrows(RuntimeException.class, () -> {
            MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
            maskingConfiguration.setValue("binning.mask.binSize", 0);
            MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

            String originalValue = "22";
            String maskedValue = maskingProvider.mask(originalValue);

            assertEquals("20-30", maskedValue);
        });
    }

    @Test
    public void testBinSizeOne() {
        assertThrows(RuntimeException.class, () -> {
            MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
            maskingConfiguration.setValue("binning.mask.binSize", 1);
            MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

            String originalValue = "22";
            String maskedValue = maskingProvider.mask(originalValue);

            assertEquals("20-30", maskedValue);
        });
    }
    
    @Test
    public void testDouble() {
        MaskingProvider maskingProvider = new BinningMaskingProvider();

        String originalValue = "21.34";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("20-25", maskedValue);
    }

    @Test
    public void testFormat() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("binning.mask.format", "[%s-%s]");
        MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

        String originalValue = "22";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("[20-25]", maskedValue);
    }

    @Test
    public void testReturnBinMean() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("binning.mask.binSize", 5);
        maskingConfiguration.setValue("binning.mask.returnBinMean", true);
        MaskingProvider maskingProvider = new BinningMaskingProvider(maskingConfiguration);

        String originalValue = "22";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals(22.5, Double.parseDouble(maskedValue), 0.0001);
    }
}


