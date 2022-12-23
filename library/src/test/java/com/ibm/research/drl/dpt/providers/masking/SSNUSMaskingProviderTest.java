/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.SSNUS;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUSIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSNUSMaskingProviderTest {
    @Test
    public void testMaskPreserveAreaAndGroup() {
        MaskingProvider maskingProvider = new SSNUSMaskingProvider();
        Identifier identifier = new SSNUSIdentifier();

        String ssn = "123-12-1234";
        String maskedValue = maskingProvider.mask(ssn);

        assertNotEquals(maskedValue, ssn);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertTrue(maskedValue.startsWith("123-12-"));
    }

    @Test
    public void testMaskNoPreserving() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("ssnus.mask.preserveAreaNumber", false);
        configuration.setValue("ssnus.mask.preserveGroup", false);
        MaskingProvider maskingProvider = new SSNUSMaskingProvider(configuration);
        SSNUSIdentifier identifier = new SSNUSIdentifier();

        String ssnValue = "123-12-1234";

        int randomizationOKGroup = 0;
        int randomizationOKArea = 0;

        for(int i=0; i < 1_000; i++) {
            String maskedValue = maskingProvider.mask(ssnValue);
            assertNotEquals(maskedValue, ssnValue);

            assertTrue(identifier.isOfThisType(maskedValue));
            SSNUS ssn = identifier.parseSSNUS(maskedValue);

            if(!(ssn.getAreaNumber().equals("123"))) {
                randomizationOKArea++;
            }

            if(!(ssn.getGroup().equals("12"))) {
                randomizationOKGroup++;
            }
        }

        assertTrue(randomizationOKGroup > 0);
        assertTrue(randomizationOKArea > 0);
    }

    @Test
    public void testMaskInvalidValue() {
        MaskingProvider maskingProvider = new SSNUSMaskingProvider();
        Identifier identifier = new SSNUSIdentifier();

        String ssn = "foobar";
        String maskedValue = maskingProvider.mask(ssn);
        assertTrue(identifier.isOfThisType(maskedValue));
    }
}

