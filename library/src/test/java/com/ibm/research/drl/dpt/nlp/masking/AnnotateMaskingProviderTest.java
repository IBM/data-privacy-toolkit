/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotateMaskingProviderTest {

    @Test
    public void testMasksCorrectly() {
        String value = "foo";
        MaskingProvider maskingProvider = new AnnotateMaskingProvider(new SecureRandom(), new DefaultMaskingConfiguration());

        assertEquals("<ProviderType:NAME>" + value + "</ProviderType>", maskingProvider.mask(value, "NAME"));
    }

}

