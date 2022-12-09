/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RedactMaskingProviderTest {

    @Test
    public void testMaskDoesNotPreserveLength() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("redact.replace.character", "*");
        maskingConfiguration.setValue("redact.preserve.length", false);
        
        MaskingProvider maskingProvider = new RedactMaskingProvider(maskingConfiguration);
        
        String value = "abc";
        assertEquals("*", maskingProvider.mask(value));
    }

    @Test
    public void testMaskDoesNotPreserveLengthReturnsEmpty() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("redact.replace.character", "");
        maskingConfiguration.setValue("redact.preserve.length", false);

        MaskingProvider maskingProvider = new RedactMaskingProvider(maskingConfiguration);
       
        String value = "abc";
        assertEquals("", maskingProvider.mask(value));
    }

    @Test
    public void testMaskPreserveLength() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("redact.replace.character", "*");
        maskingConfiguration.setValue("redact.preserve.length", true);

        MaskingProvider maskingProvider = new RedactMaskingProvider(maskingConfiguration);

        String value = "abc";
        assertEquals("***", maskingProvider.mask(value));
    }
}
