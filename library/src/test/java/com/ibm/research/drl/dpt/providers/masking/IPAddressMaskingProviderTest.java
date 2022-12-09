/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPAddressMaskingProviderTest {

    @Test
    public void testIPv4Mask() {
        MaskingProvider ccMaskingProvider = new IPAddressMaskingProvider();

        String originalIPv4Address = "122.133.10.198";
        String maskedResult = ccMaskingProvider.mask(originalIPv4Address);

        // basic test that we do not return the same
        assertNotEquals(originalIPv4Address, maskedResult);

        // basic test to check that returned masked respects the format
        IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();
        assertTrue(ipAddressIdentifier.isOfThisType(maskedResult));

        //basic test to check that every time we randomize the result
        String maskedResult2 = ccMaskingProvider.mask(originalIPv4Address);
        assertNotEquals(maskedResult, maskedResult2);
    }

    @Test
    public void testIPV4PreserveSubnets() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("ipaddress.subnets.preserve", 2);
        IPAddressMaskingProvider maskingProvider = new IPAddressMaskingProvider(configuration);

        String originalIPv4Address = "122.133.10.198";
        String maskedResult = maskingProvider.mask(originalIPv4Address);
        assertNotEquals(originalIPv4Address, maskedResult);
        assertTrue(maskedResult.startsWith("122.133."));
    }

    @Test
    public void testIPv6Mask() {
        MaskingProvider ccMaskingProvider = new IPAddressMaskingProvider();

        String[] validIPv6Addresses = {
                "1:2:3:4:5:6:7:8",
                "1::",
                "1::8",
                "1::7:8",
                "1::6:7:8",
                "1::5:6:7:8",
                "1::4:5:6:7:8",
                "1::3:4:5:6:7:8",
                "fe80::7:8%eth0",
                "::255.255.255.255",
                "::ffff:255.255.255.255",
                "::FFFF:255.255.255.255",
                "::ffff:0:255.255.255.255",
                "::FFFF:0:255.255.255.255",
                "2001:db8:3:4::192.0.2.33",
                "64:ff9b::192.0.2.33"
        };

        IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();

        for(String ipv6address: validIPv6Addresses) {
            String maskedResult = ccMaskingProvider.mask(ipv6address);
            assertNotEquals(maskedResult, ipv6address);
            assertTrue(ipAddressIdentifier.isIPv6(maskedResult), maskedResult);
        }

    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration preserveConfiguration = new DefaultMaskingConfiguration("preserve");
        preserveConfiguration.setValue("ipaddress.subnets.preserve", 2);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, preserveConfiguration
        };

        String[] originalValues = new String[]{"122.133.10.198", "1:2:3:4:5:6:7:8"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            IPAddressMaskingProvider maskingProvider = new IPAddressMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.println(String.format("%s: %s: %d operations took %d milliseconds (%f per op)",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N));
            }
        }
    }

    @Test
    public void testMaskInvalidValue() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.GENERATE_RANDOM);

        IPAddressMaskingProvider maskingProvider = new IPAddressMaskingProvider(maskingConfiguration);
        IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();

        String invalidValue = "adadads";
        String maskedValue = maskingProvider.mask(invalidValue);

        assertNotEquals(maskedValue, invalidValue);
        assertTrue(ipAddressIdentifier.isOfThisType(maskedValue));
    }

    @Test
    public void testMaskInvalidValueReturnEmpty() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_EMPTY);

        IPAddressMaskingProvider maskingProvider = new IPAddressMaskingProvider(maskingConfiguration);

        String invalidValue = "adadads";
        String maskedValue = maskingProvider.mask(invalidValue);
        assertTrue(maskedValue.isEmpty());
    }
}
