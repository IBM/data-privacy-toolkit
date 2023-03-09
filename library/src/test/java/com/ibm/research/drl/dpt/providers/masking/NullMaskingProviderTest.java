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