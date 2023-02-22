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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ATCMaskingProviderTest {

    @Test
    public void testMaskWithinAcceptableLevels() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();

        String atc = "A04AA02";

        configuration.setValue("atc.mask.levelsToKeep", 1);
        ATCMaskingProvider maskingProvider = new ATCMaskingProvider(configuration);
        String maskedValue = maskingProvider.mask(atc);
        assertEquals("A", maskedValue, maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 2);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04", maskedValue, maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 3);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04A", maskedValue, maskedValue);

        configuration.setValue("atc.mask.levelsToKeep", 4);
        maskingProvider = new ATCMaskingProvider(configuration);
        maskedValue = maskingProvider.mask(atc);
        assertEquals("A04AA", maskedValue, maskedValue);
    }

    @Test
    public void testMaskingOutsideAcceptableGeneralizationLevels() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        String atc = "A04AA02";

        configuration.setValue("atc.mask.levelsToKeep", 5);
        ATCMaskingProvider maskingProvider = new ATCMaskingProvider(configuration);

        int count = 0;

        for (int i = 0; i < 100; i++) {
            String masked = maskingProvider.mask(atc);

            if (masked.equals(atc)) {
                count += 1;
            }
        }

        assertThat(count, lessThan(5));
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

