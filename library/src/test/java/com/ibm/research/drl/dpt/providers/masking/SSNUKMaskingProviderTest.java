/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUKIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SSNUKMaskingProviderTest {

    @Test
    public void testMaskPrefixPreservation() {
        MaskingProvider maskingProvider = new SSNUKMaskingProvider();
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";
        String maskedValue = maskingProvider.mask(ssn);

        assertNotEquals(maskedValue, ssn);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertTrue(maskedValue.startsWith("AB"));
    }

    @Test
    public void testMaskNoPrefixPreservation() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("ssnuk.mask.preservePrefix", false);
        MaskingProvider maskingProvider = new SSNUKMaskingProvider(configuration);
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";

        int count = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(ssn);
            assertNotEquals(maskedValue, ssn);
            assertTrue(identifier.isOfThisType(maskedValue));

            if(!maskedValue.startsWith("AB")) {
                count++;
            }
        }

        assertTrue(count > 0);
    }

}

