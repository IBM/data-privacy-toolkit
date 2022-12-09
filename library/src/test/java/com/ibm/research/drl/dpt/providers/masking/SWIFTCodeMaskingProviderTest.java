/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.SWIFTCodeIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SWIFTCodeMaskingProviderTest {

    @Test
    public void testDefault() {
        MaskingProvider maskingProvider = new SWIFTCodeMaskingProvider();
        Identifier identifier = new SWIFTCodeIdentifier();

        String key = "EMCRGRA1";
        String maskedValue = maskingProvider.mask(key);
        assertTrue(identifier.isOfThisType(maskedValue));
    }

    @Test
    public void testPreserveCountry() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("swift.mask.preserveCountry", true);
        MaskingProvider maskingProvider = new SWIFTCodeMaskingProvider(maskingConfiguration);
        Identifier identifier = new SWIFTCodeIdentifier();

        String key = "EMCRGRA1";
        String countryCodeOriginal = key.substring(4, 6);
        String maskedValue = maskingProvider.mask(key);
        String countryCodeMasked = maskedValue.substring(4, 6);

        System.out.println(maskedValue);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertEquals(countryCodeOriginal, countryCodeMasked);
    }
}

