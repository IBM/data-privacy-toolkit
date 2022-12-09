/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.Address;
import com.ibm.research.drl.dpt.providers.identifiers.AddressIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AddressMaskingProviderTest {

    @Test
    public void testMask() {
        AddressIdentifier identifier = new AddressIdentifier();

        String validAddress = "200 E Main St, Phoenix AZ 85123, USA";
        Address originalAddress = identifier.parseAddress(validAddress);
        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider();

        int randomizationOK = 0;

        for(int i = 0; i < 1000; i++) {
            String randomAddress = addressMaskingProvider.mask(validAddress);

            assertNotEquals(randomAddress, validAddress);

            Address maskedAddress = identifier.parseAddress(randomAddress);

            if (maskedAddress == null) {
                System.out.println(randomAddress);
            }

            assertNotNull(maskedAddress);

            if(!originalAddress.getNumber().equals(maskedAddress.getNumber())) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testMaskShortAddress() {
        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider();

        String validAddress = "200 E Main St";
        String randomAddress = addressMaskingProvider.mask(validAddress);
        assertNotEquals(randomAddress, validAddress);
    }

    @Test
    public void testMaskPseudorandom() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("address.mask.pseudorandom", true);

        AddressIdentifier identifier = new AddressIdentifier();
        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider(maskingConfiguration);

        String[] validAddresses = {
                "200 E Main St, Phoenix AZ 85123, USA",
                "200 E Main St"
        };

        for(String validAddress: validAddresses) {
            String randomAddress = addressMaskingProvider.mask(validAddress);
            assertTrue(identifier.isOfThisType(randomAddress));
            assertNotEquals(randomAddress, validAddress);

            for (int i = 0; i < 100; i++) {
                String rnd = addressMaskingProvider.mask(validAddress);
                assertEquals(randomAddress, rnd);
            }
        }
    }

    @Test
    public void testMaskPOBoxPseudorandom() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("address.mask.pseudorandom", true);

        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider(configuration);
        AddressIdentifier identifier = new AddressIdentifier();

        String validAddress = "PO BOX 1234";
        String randomAddress = addressMaskingProvider.mask(validAddress);

        assertNotEquals(randomAddress, validAddress);

        Address originalAddress = identifier.parseAddress(validAddress);
        Address maskedAddress = identifier.parseAddress(randomAddress);

        assertNotNull(maskedAddress);
        assertTrue(maskedAddress.isPOBox());
        assertNotEquals(maskedAddress.getPoBoxNumber(), originalAddress.getPoBoxNumber());

        for(int i = 0; i < 1000; i++) {
            String rnd = addressMaskingProvider.mask(validAddress);
            assertEquals(randomAddress, rnd);
        }
    }

    @Test
    public void testMaskPOBox() {
        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider();
        AddressIdentifier identifier = new AddressIdentifier();

        String validAddress = "PO BOX 1234";
        String randomAddress = addressMaskingProvider.mask(validAddress);

        assertNotEquals(randomAddress, validAddress);

        Address originalAddress = identifier.parseAddress(validAddress);
        Address maskedAddress = identifier.parseAddress(randomAddress);

        assertNotNull(maskedAddress);
        assertTrue(maskedAddress.isPOBox());
        assertNotEquals(maskedAddress.getPoBoxNumber(), originalAddress.getPoBoxNumber());
    }

    @Test
    public void testMaskInvalidAddressAsInput() {
        AddressMaskingProvider addressMaskingProvider = new AddressMaskingProvider();
        AddressIdentifier identifier = new AddressIdentifier();

        String invalidAddress = "PA BOX 1234";
        String randomAddress = addressMaskingProvider.mask(invalidAddress);

        assertNotEquals(randomAddress, invalidAddress);
            assertTrue(identifier.isOfThisType(randomAddress));
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration nearestConfiguration = new DefaultMaskingConfiguration("nearest postal");
        nearestConfiguration.setValue("address.postalCode.nearest", true);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, nearestConfiguration
        };

        String[] originalValues = new String[]{
                "200 E Main St, Phoenix AZ 85123, USA",
                "PO BOX 1234"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            AddressMaskingProvider maskingProvider = new AddressMaskingProvider(maskingConfiguration);

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
