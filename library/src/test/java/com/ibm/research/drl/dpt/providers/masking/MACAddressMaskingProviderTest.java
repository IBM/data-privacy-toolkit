/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.MACAddressIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MACAddressMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        MACAddressMaskingProvider maskingProvider = new MACAddressMaskingProvider();
        MACAddressIdentifier identifier = new MACAddressIdentifier();

        String originalValue = "00:0a:95:9d:68:16";

        String maskedValue = maskingProvider.mask(originalValue);
        System.out.println(maskedValue);

        assertTrue(identifier.isOfThisType(maskedValue));
        assertNotEquals(maskedValue, originalValue);
        assertTrue(maskedValue.toLowerCase().startsWith("00:0a:95:"));
    }

    @Test
    public void testMaskNoVendorPreservation() throws Exception {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("mac.masking.preserveVendor", false);
        MACAddressMaskingProvider maskingProvider = new MACAddressMaskingProvider(configuration);
        MACAddressIdentifier identifier = new MACAddressIdentifier();

        String originalValue = "00:0a:95:9d:68:16";

        String maskedValue = maskingProvider.mask(originalValue);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertNotEquals(maskedValue, originalValue);
        assertFalse(maskedValue.toLowerCase().startsWith("00:0a:95:"));
    }

    @Test
    public void testMaskInvalidValue() throws Exception {
        MACAddressMaskingProvider maskingProvider = new MACAddressMaskingProvider();
        MACAddressIdentifier identifier = new MACAddressIdentifier();

        String invalidValue = "foobar";
        String maskedValue = maskingProvider.mask(invalidValue);

        assertNotEquals(maskedValue, invalidValue);
        assertTrue(identifier.isOfThisType(maskedValue));
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration nopreserveConfiguration = new DefaultMaskingConfiguration("nopreserve");
        nopreserveConfiguration.setValue("mac.masking.preserveVendor", false);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, nopreserveConfiguration
        };

        String[] originalValues = new String[]{
                "00:0a:95:9d:68:16"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            MACAddressMaskingProvider maskingProvider = new MACAddressMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}
