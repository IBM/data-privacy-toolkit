/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.providers.identifiers.GenderIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenderMaskingProviderTest {

    @Test
    public void testMask() {
        Identifier identifier = new GenderIdentifier();
        MaskingProvider maskingProvider = new GenderMaskingProvider();

        String originalValue = "male";
        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertTrue(identifier.isOfThisType(maskedValue));
            if (!maskedValue.equals(originalValue)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }
}


