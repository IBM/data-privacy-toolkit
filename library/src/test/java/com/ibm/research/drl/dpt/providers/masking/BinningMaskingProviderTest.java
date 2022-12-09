/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
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


