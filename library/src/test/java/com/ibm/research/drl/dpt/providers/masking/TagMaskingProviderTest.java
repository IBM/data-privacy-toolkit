/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagMaskingProviderTest {


    @Test
    public void testMasksCorrectly() {
        MaskingProvider maskingProvider = new TagMaskingProvider(new SecureRandom(), new DefaultMaskingConfiguration());

        String value = "foo";

        String masked = maskingProvider.mask(value, "NAME");
        assertEquals("NAME-0", masked);
        masked = maskingProvider.mask(value, "NAME");
        assertEquals("NAME-0", masked);

        masked = maskingProvider.mask(value, "CITY");
        assertEquals("CITY-0", masked);
        masked = maskingProvider.mask("anothervalue", "CITY");
        assertEquals("CITY-1", masked);
    }
}

