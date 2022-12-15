/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.CountyIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountyMaskingProviderTest {

    @Test
    public void testPseudorandom() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("county.mask.pseudorandom", true);

        MaskingProvider maskingProvider = new CountyMaskingProvider(maskingConfiguration);

        String originalCity = "Italy";
        String firstMask = maskingProvider.mask(originalCity);

        for(int i = 0; i < 100; i++) {
            String maskedCity = maskingProvider.mask(originalCity);
            assertEquals(firstMask, maskedCity);
        }

    }

    @Test
    public void testMask() {
        Identifier identifier = new CountyIdentifier();
        MaskingProvider maskingProvider = new CountyMaskingProvider();

        String originalValue = "Pendleton County";
        assertTrue(identifier.isOfThisType(originalValue));

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertTrue(identifier.isOfThisType(maskedValue));

            if (!maskedValue.equals(originalValue)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }
}

