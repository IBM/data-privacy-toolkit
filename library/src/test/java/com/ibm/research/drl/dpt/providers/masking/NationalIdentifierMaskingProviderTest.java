/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;


public class NationalIdentifierMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        NationalIdentifierMaskingProvider mp = new NationalIdentifierMaskingProvider(new SecureRandom());//123456789));

        assertThat(mp.mask("123").length(), is("123".length()));

        assertThat(mp.mask("123-123-123"), not("123-123-123"));
        assertThat(mp.mask("ccc-ccc-ccc"), not("ccc-ccc-ccc"));

        String out = mp.mask("12dfa3-2342ccc-1dfa3342d23");
        assertThat(out.charAt(6), is('-'));
        assertThat(out.charAt(14), is('-'));
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{
                "123-123-123"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            NationalIdentifierMaskingProvider maskingProvider = new NationalIdentifierMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}
