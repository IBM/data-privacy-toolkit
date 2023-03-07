/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class HashMaskingProviderTest {
    @Test
    public void testMask() throws Exception {
        HashMaskingProvider maskingProvider = new HashMaskingProvider();

        String value = "test";
        //sha-256 by default
        assertTrue(maskingProvider.mask(value).equalsIgnoreCase("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"));
        assertTrue(maskingProvider.mask(value).equalsIgnoreCase("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"));
    }

    @Test
    public void testMaskWithNormalize() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("hashing.normalize", true);

        HashMaskingProvider hashMaskingProvider = new HashMaskingProvider(maskingConfiguration);

        String value = "Joe";
        String maskedValue1 = hashMaskingProvider.mask(value);

        String value2 = "JOE";
        String maskedValue2 = hashMaskingProvider.mask(value2);

        assertEquals(maskedValue1, maskedValue2);
    }
    @Test
    public void testMaskSalt() throws Exception {
        String originalValue = "foobar";
        
        MaskingConfiguration configurationNoSalt = new DefaultMaskingConfiguration();
        configurationNoSalt.setValue("hashing.salt", "");
        HashMaskingProvider maskingProviderNoSalt = new HashMaskingProvider(configurationNoSalt);

        MaskingConfiguration configurationWithSalt = new DefaultMaskingConfiguration();
        configurationWithSalt.setValue("hashing.salt", "randomsalt");
        HashMaskingProvider maskingProviderWithSalt = new HashMaskingProvider(configurationWithSalt);

        assertNotEquals(maskingProviderNoSalt.mask(originalValue), maskingProviderWithSalt.mask(originalValue));
    }
}
