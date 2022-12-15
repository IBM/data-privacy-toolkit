/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class NullMaskingProviderTest {

    @Test
    void testI382NullMaskingProviderShouldCorrectlyHandleObjects() {
        DefaultMaskingConfiguration defaultMaskingConfigurationWithNull = new DefaultMaskingConfiguration();
        defaultMaskingConfigurationWithNull.setValue("null.mask.returnNull", true);

        NullMaskingProvider nullMaskingProviderWithNull = new NullMaskingProvider(defaultMaskingConfigurationWithNull);
        byte[] maskedValueWithNull = nullMaskingProviderWithNull.mask(new Object(), "foo");
        assertThat(maskedValueWithNull, is(nullValue()));

        DefaultMaskingConfiguration defaultMaskingConfigurationWithString = new DefaultMaskingConfiguration();
        defaultMaskingConfigurationWithString.setValue("null.mask.returnNull", false);

        NullMaskingProvider nullMaskingProviderWithString = new NullMaskingProvider(defaultMaskingConfigurationWithString);
        byte[] maskedValueWithString = nullMaskingProviderWithString.mask(new Object(), "foo");
        assertThat(maskedValueWithString, is("".getBytes()));
    }
}