/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IBANIdentifier;
import org.iban4j.Iban;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Ignoring until we found a better generator")
public class IBANMaskingProviderTest {

    @Test
    public void testMask() {
        IBANMaskingProvider maskingProvider = new IBANMaskingProvider();
        IBANIdentifier identifier = new IBANIdentifier();

        String iban = "IE71WZXH31864186813343";
        String maskedValue = maskingProvider.mask(iban);

        assertNotEquals(maskedValue, iban);
        assertTrue(identifier.isOfThisType(maskedValue));
        //by default we preserve the country
        assertTrue(maskedValue.startsWith("IE"));
    }

    @Test
    public void testMaskNoCountryPreservation() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("iban.mask.preserveCountry", false);
        IBANMaskingProvider maskingProvider = new IBANMaskingProvider(configuration);
        IBANIdentifier identifier = new IBANIdentifier();

        String iban = "IE71WZXH31864186813343";

        int randomizationOK = 0;

        for(int i = 0; i < 10; i++) {
            String maskedValue = maskingProvider.mask(iban);
            assertNotEquals(maskedValue, iban);
            assertTrue(identifier.isOfThisType(maskedValue), maskedValue);
            if(!maskedValue.startsWith("IE")) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testMaskInvalidValue() {
        IBANMaskingProvider maskingProvider = new IBANMaskingProvider();
        IBANIdentifier identifier = new IBANIdentifier();

        String iban = "foobar";

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(iban);
            assertNotEquals(maskedValue, iban, iban);
            assertTrue(identifier.isOfThisType(maskedValue), maskedValue);
        }
    }

    @Test
    public void checkIbanGeneratorIsGood() {
        IBANIdentifier identifier = new IBANIdentifier();

        for (int i = 0; i < 100; ++i) {
            String iban = Iban.random().toString();

            assertTrue(identifier.isOfThisType(iban), iban);
        }
    }
}
