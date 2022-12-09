/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZIPCodeMaskingProviderTest {

    @Test
    public void testMinimumPopulationNoPrefixDoesNotMatchMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 20000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 18750
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("000", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationNoPrefixMatchesMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 18000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 18750
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("00601", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationWithPrefixMatchesMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 18000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 1214568
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("006", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationWithPrefixDoesNotMatchMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 10000000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 1214568
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("000", maskedZipcode);
    }

    @Test
    public void testNoMinimumPopulation() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 10000000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601";

        int randomOK = 0;

        for(int i = 0; i < 1000; i++) {
            String maskedZipcode = maskingProvider.mask(zipcode);
            if(!maskedZipcode.equals(zipcode)) {
                randomOK++;
            }
        }

        assertTrue(randomOK > 0);
    }
}

