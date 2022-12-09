/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.PhoneNumber;
import com.ibm.research.drl.dpt.providers.identifiers.PhoneIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneMaskingProviderTest {

    @Test
    public void testMask() {
        PhoneMaskingProvider phoneMaskingProvider = new PhoneMaskingProvider();
        PhoneIdentifier phoneIdentifier = new PhoneIdentifier();

        String originalPhone = "+353-0876653255";
        PhoneNumber originalPhoneNumber = phoneIdentifier.getPhoneNumber(originalPhone);

        for(int i = 0; i < 1000; i++) {
            String maskedPhone = phoneMaskingProvider.mask(originalPhone);

            PhoneNumber maskedPhoneNumber = phoneIdentifier.getPhoneNumber(maskedPhone);

            assertEquals(originalPhoneNumber.getPrefix(), maskedPhoneNumber.getPrefix());
            assertEquals(originalPhoneNumber.getSeparator(), maskedPhoneNumber.getSeparator());

            assertEquals(originalPhoneNumber.getCountryCode(), maskedPhoneNumber.getCountryCode());
            assertNotEquals(originalPhoneNumber.getNumber(), maskedPhoneNumber.getNumber());
        }

        //case we dont preserve the country code
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("phone.countryCode.preserve", false);
        phoneMaskingProvider = new PhoneMaskingProvider(configuration);

        int randomizationOKCC = 0;
        for(int i = 0; i < 1000; i++) {
            String maskedPhone = phoneMaskingProvider.mask(originalPhone);
            PhoneNumber maskedPhoneNumber = phoneIdentifier.getPhoneNumber(maskedPhone);
            if(!originalPhoneNumber.getCountryCode().equals(maskedPhoneNumber.getCountryCode())) {
                randomizationOKCC++;
            }
        }

        assertTrue(randomizationOKCC > 0);
    }

    @Test
    public void testMaskUSNumber() {
        PhoneMaskingProvider phoneMaskingProvider = new PhoneMaskingProvider();
        PhoneIdentifier phoneIdentifier = new PhoneIdentifier();

        String originalPhone = "3471234567";
        String maskedPhone = phoneMaskingProvider.mask(originalPhone);

        assertNotEquals(originalPhone, maskedPhone);
        //by default we preserve area codes
        assertTrue(maskedPhone.startsWith("347"));

        originalPhone = "+1-3471234567";
        maskedPhone = phoneMaskingProvider.mask(originalPhone);

        assertNotEquals(originalPhone, maskedPhone);
        //by default we preserve area codes
        assertTrue(maskedPhone.startsWith("+1-347"));
    }

    @Test
    public void testMaskUSNumberNoAreaCode() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("phone.areaCode.preserve", false);
        PhoneMaskingProvider phoneMaskingProvider = new PhoneMaskingProvider(configuration);

        String originalPhone = "+1-3471234567";

        int randomOK = 0;

        for(int i = 0; i < 1000; i++) {
            String maskedPhone = phoneMaskingProvider.mask(originalPhone);
            assertNotEquals(originalPhone, maskedPhone);

            if(!maskedPhone.startsWith("+1-347")) {
                randomOK++;
            }
        }

        assertTrue(randomOK > 0);
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{
                "+353-0876653255",
                "3471234567"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            PhoneMaskingProvider maskingProvider = new PhoneMaskingProvider(maskingConfiguration);

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

    @Test
    public void testMaskInvalidValue() {
        PhoneMaskingProvider phoneMaskingProvider = new PhoneMaskingProvider();
        PhoneIdentifier phoneIdentifier = new PhoneIdentifier();

        String invalidValue = "junk";
        String maskedPhone = phoneMaskingProvider.mask(invalidValue);

        assertNotEquals(maskedPhone, invalidValue);
        assertTrue(phoneIdentifier.isOfThisType(maskedPhone));
    }
}
