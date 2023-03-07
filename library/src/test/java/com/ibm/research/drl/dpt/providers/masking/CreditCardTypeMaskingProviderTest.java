/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.CreditCardTypeIdentifier;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Map<String, OriginalMaskedValuePair> maskedValues = new HashMap<>();
        maskedValues.put("cc", new OriginalMaskedValuePair("41223333333312345", "5523527012345678"));

        for (int i = 0; i < 100; i++) {
            String maskedCCType = maskingProvider.maskLinked(originalCCType, "5523527012345678");
            assertTrue("Mastercard".equalsIgnoreCase(maskedCCType));
        }
    }
}

