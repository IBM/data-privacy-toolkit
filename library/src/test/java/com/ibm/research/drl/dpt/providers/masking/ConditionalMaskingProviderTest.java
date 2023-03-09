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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ConditionalMaskingProviderTest {

    @Test
    public void conditionalMaskingProvidersAreReturned() {
        ObjectMapper mapper = new ObjectMapper();

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("conditional.isWhitelist", true);
        configuration.setValue("conditional.patterns", mapper.createArrayNode().add("foo"));
        configuration.setValue("conditional.fieldName", "foo");
        configuration.setValue("conditional.providerName", "HASH");

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());

        MaskingProvider maskingProvider = factory.get(ProviderType.valueOf("CONDITIONAL"), configuration);

        assertNotNull(maskingProvider);
    }

    @Test
    public void doesNotMaskWhiteListedValues() {
        ObjectMapper mapper = new ObjectMapper();

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("conditional.isWhitelist", true);
        configuration.setValue("conditional.patterns", mapper.createArrayNode().add("foo"));
        configuration.setValue("conditional.fieldName", "foo");
        configuration.setValue("conditional.providerName", "HASH");

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());

        MaskingProvider maskingProvider = factory.get(ProviderType.valueOf("CONDITIONAL"), configuration);

        assertNotNull(maskingProvider);

        String notMatching = "bar";
        String matching = "foo";

        assertThat(maskingProvider.mask(notMatching), is(not(notMatching)));
        assertThat(maskingProvider.mask(matching), is(matching));
    }

    @Test
    public void doesMaskOnlyBlackListedValues() {
        ObjectMapper mapper = new ObjectMapper();

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("conditional.isWhitelist", false);
        configuration.setValue("conditional.patterns", mapper.createArrayNode().add("foo"));
        configuration.setValue("conditional.fieldName", "foo");
        configuration.setValue("conditional.providerName", "HASH");

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());

        MaskingProvider maskingProvider = factory.get(ProviderType.valueOf("CONDITIONAL"), configuration);

        assertNotNull(maskingProvider);

        String notMatching = "bar";
        String matching = "foo";

        assertThat(maskingProvider.mask(notMatching), is(notMatching));
        assertThat(maskingProvider.mask(matching), is(not(matching)));
    }

    @Test
    public void doesNotMaskWhiteListedValuesCaseInsensitive() {
        ObjectMapper mapper = new ObjectMapper();

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("conditional.isWhitelist", true);
        configuration.setValue("conditional.patterns", mapper.createArrayNode().add(
                mapper.createObjectNode()
                        .put("regex","foo")
                        .put("caseInsensitive", true)
        ));
        configuration.setValue("conditional.fieldName", "foo");
        configuration.setValue("conditional.providerName", "HASH");

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());

        MaskingProvider maskingProvider = factory.get(ProviderType.valueOf("CONDITIONAL"), configuration);

        assertNotNull(maskingProvider);

        String notMatching = "bar";
        String[] matchings = {"foo", "FOO", "fOo"};

        assertThat(maskingProvider.mask(notMatching), is(not(notMatching)));

        for (String matching: matchings) {
            assertThat(maskingProvider.mask(matching), is(matching));
        }
    }

    @Test
    public void doesMaskOnlyBlackListedValuesCaseInsensitive() {
        ObjectMapper mapper = new ObjectMapper();

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("conditional.isWhitelist", false);
        configuration.setValue("conditional.patterns", mapper.createArrayNode().add(
                mapper.createObjectNode()
                        .put("regex","foo")
                        .put("caseInsensitive", true)
        ));
        configuration.setValue("conditional.fieldName", "foo");
        configuration.setValue("conditional.providerName", "HASH");

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());

        MaskingProvider maskingProvider = factory.get(ProviderType.valueOf("CONDITIONAL"), configuration);

        assertNotNull(maskingProvider);

        String notMatching = "bar";
        String[] matchings = {"foo", "FOO", "fOo"};

        assertThat(maskingProvider.mask(notMatching), is(notMatching));

        for (String matching: matchings) {
            assertThat(maskingProvider.mask(matching), is(not(matching)));
        }
    }


}