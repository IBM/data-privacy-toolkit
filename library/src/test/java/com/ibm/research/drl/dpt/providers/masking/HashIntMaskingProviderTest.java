/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HashIntMaskingProviderTest {


    @Test
    public void signCoherent() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("hashint.sign.coherent", true);

        HashIntMaskingProvider maskingProvider = new HashIntMaskingProvider(new SecureRandom(), maskingConfiguration);

        Random random = new Random();

        for (int i = 0; i < 10; ++i) {
            long original = random.nextLong();

            String masked = maskingProvider.mask(Long.toString(original));

            assertNotEquals(original, Long.parseLong(masked));
            assertNotEquals(Long.toString(original), masked);
            assertThat(Long.signum(Long.parseLong(masked)), is(Long.signum(original)));
        }
    }


    @Test
    public void testMask() {
        HashIntMaskingProvider maskingProvider = new HashIntMaskingProvider();

        String value = "12391312312";

        //sha-256 by default
        String masked = maskingProvider.mask(value);

        assertNotEquals(value, masked);
    }
}

