/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.CreditCardIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.*;

public class CreditCardMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider ccMaskingProvider = new CreditCardMaskingProvider(new SecureRandom());//1234567890));

        // different values
        assertThat(ccMaskingProvider.mask("123456789"), not("123456789"));
        assertNotEquals("123456789", ccMaskingProvider.mask("123456789"));

        String test = "123-123-123";
        String res = ccMaskingProvider.mask(test);

        // same length
        assertThat(res.length(), is(test.length()));

        // same pattern
        for (int i = 0; i < test.length(); ++i) {
            assertThat(Character.isDigit(res.charAt(i)), is(Character.isDigit(test.charAt(i))));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration nopreserveMaskingConfiguration = new DefaultMaskingConfiguration("nopreserve");
        nopreserveMaskingConfiguration.setValue("creditCard.issuer.preserve", false);

        DefaultMaskingConfiguration[] maskingConfigurations = new DefaultMaskingConfiguration[]{
                defaultMaskingConfiguration, nopreserveMaskingConfiguration
        };

        for (DefaultMaskingConfiguration maskingConfiguration : maskingConfigurations) {
            CreditCardMaskingProvider maskingProvider = new CreditCardMaskingProvider(maskingConfiguration);

            int N = 1000000;
            String originalCC = "5523527012345678";

            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                String maskedValue = maskingProvider.mask(originalCC);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                    maskingConfiguration.getName(), N, diff, (double) diff / N);
        }
    }

    @Test
    public void testPreservesIssuer() {
        MaskingProvider ccMaskingProvider = new CreditCardMaskingProvider();
        CreditCardIdentifier identifier = new CreditCardIdentifier();

        String originalCC = "5584637593005095";
        String maskedCC = ccMaskingProvider.mask(originalCC);

        assertEquals(originalCC.length(), maskedCC.length());
        assertNotEquals(originalCC, maskedCC);

        assertTrue(maskedCC.startsWith("558463"));
        assertTrue(identifier.isOfThisType(maskedCC));
    }

    @Test
    public void testNoIssuer() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("creditCard.issuer.preserve", false);

        CreditCardMaskingProvider ccMaskingProvider = new CreditCardMaskingProvider(configuration);

        String originalCC = "5584637593005095";

        int preserved = 0;

        for (int i = 0; i < 100; ++i) {
            String maskedCC = ccMaskingProvider.mask(originalCC);
            assertNotEquals(originalCC, maskedCC);

            preserved += maskedCC.startsWith("5584") ? 1 : 0;
        }

        assertThat(preserved, lessThan(10));
    }
}
