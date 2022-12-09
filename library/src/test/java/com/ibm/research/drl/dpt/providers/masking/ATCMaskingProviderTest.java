/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.ATCIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ATCMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();

        String atc = "A04AA02";

        configuration.setValue("atc.mask.levelsToKeep", 1);
        MaskingProvider maskingProvider = new ATCMaskingProvider(configuration);
        String maskedValue = maskingProvider.mask(atc);
        assertEquals("A", maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 2);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04", maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 3);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04A", maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 4);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04AA", maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 5);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertNotEquals(maskedValue, atc);
    }

    @Test
    public void testMaskInvalidValue() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        MaskingProvider maskingProvider = new ATCMaskingProvider(configuration);

        String atc = "foobar";
        String maskedValue = maskingProvider.mask(atc);
        assertTrue(new ATCIdentifier().isOfThisType(maskedValue));
        assertNotEquals(maskedValue, atc);
    }

}

