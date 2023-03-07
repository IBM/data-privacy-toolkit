/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.providers.identifiers.CreditCardTypeIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreditCardTypeMaskingProviderTest {

    @Test
    public void testMask() {
        CreditCardTypeMaskingProvider maskingProvider = new CreditCardTypeMaskingProvider();
        CreditCardTypeIdentifier identifier = new CreditCardTypeIdentifier();

        String originalValue = "VISA";

        int nonMatches = 0;
        for (int i = 0; i < 1000; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertTrue(identifier.isOfThisType(maskedValue));
            if (!maskedValue.equals(originalValue)) {
                nonMatches++;
            }
        }

        assertTrue(nonMatches > 0);
    }

    @Test
    public void testCompoundMask() {
        CreditCardTypeMaskingProvider maskingProvider = new CreditCardTypeMaskingProvider();

        String originalCCType = "VISA";

        for (int i = 0; i < 100; i++) {
            String maskedCCType = maskingProvider.maskLinked(originalCCType, "5523527012345678");
            assertTrue("Mastercard".equalsIgnoreCase(maskedCCType));
        }
    }
}

