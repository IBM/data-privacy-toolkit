/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GUIDMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider guidMaskingProvider = new GUIDMaskingProvider(new SecureRandom(), new DefaultMaskingConfiguration());

        String value = "foo";
        String masked = guidMaskingProvider.mask(value);

        UUID uuid = UUID.fromString(masked);
        assertEquals(uuid.toString().length(), UUID.randomUUID().toString().length());
    }
}
