/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.StatesUSIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatesUSMaskingProviderTest {

    @Test
    public void testMask() {
        Identifier statesUSIdentifer = new StatesUSIdentifier();
        MaskingProvider maskingProvider = new StatesUSMaskingProvider();

        String value = "Alabama";
        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            assertTrue(statesUSIdentifer.isOfThisType(maskedValue));

            if (!maskedValue.equals(value)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testMaskEmptyValue() {
        Identifier statesUSIdentifer = new StatesUSIdentifier();
        MaskingProvider maskingProvider = new StatesUSMaskingProvider();

        String value = "";
        int randomizationOK = 0;

        String maskedValue = maskingProvider.mask(value);
        assertTrue(statesUSIdentifer.isOfThisType(maskedValue));

    }
}

