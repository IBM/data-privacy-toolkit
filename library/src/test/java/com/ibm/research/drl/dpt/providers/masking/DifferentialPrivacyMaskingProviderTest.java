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

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.Mechanism;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DifferentialPrivacyMaskingProviderTest {
    @Test
    public void testThrowExceptionOnNoSpecifiedMechanism() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.mechanism", null);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String value = provider.mask("1234");

            assertNotNull(value);
            assertThat(value,not("1234"));
        });
    }

    @Test
    public void worksOnDefaultConfiguration() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
        String value = provider.mask("1234");

        assertNotNull(value);
        assertThat(value, not("1234"));
    }

    @Test
    public void testThrowExceptionOnNoEpsilon() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.parameter.epsilon", null);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String value = provider.mask("1234");

            assertNotNull(value);
            assertThat(value, not("1234"));
        });
    }

    @Test
    public void testThrowExceptionOnNegEpsilon() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.mechanism", Mechanism.BINARY.name());
            configuration.setValue("differentialPrivacy.binary.value1", "1");
            configuration.setValue("differentialPrivacy.binary.value2", "2");
            configuration.setValue("differentialPrivacy.parameter.epsilon", -1.0);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String value = provider.mask("1");

            assertNotNull(value);
            assertThat(value, is(in(new String[]{"1", "2"})));
        });
    }

    @Test
    public void worksForBinary() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("differentialPrivacy.mechanism", Mechanism.BINARY.name());
        configuration.setValue("differentialPrivacy.binary.value1", "1");
        configuration.setValue("differentialPrivacy.binary.value2", "2");
        configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
        String value = provider.mask("1");

        assertNotNull(value);
        assertThat(value, is(in(new String[]{"1", "2"})));
    }

    @Test
    public void worksForCategorical() throws Exception {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("differentialPrivacy.mechanism", Mechanism.CATEGORICAL.name());
        configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
        configuration.setValue("differentialPrivacy.categorical.hierarchyName", "colors");
        
        JsonNode hierarchyMap = JsonUtils.MAPPER.readTree("{" +
                "\"colors\": [" +
                "[\"Red\", \"*\"]," +
                "[\"Green\", \"*\"]," +
                "[\"Blue\", \"*\"]" +
                "]" +
                "}");
        
        configuration.setValue("differentialPrivacy.categorical.hierarchyMap", hierarchyMap);
        
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
        Set<String> terms = new HashSet<>(Arrays.asList("RED", "GREEN", "BLUE"));
        
        for(int i = 0; i < 100; i++) {
            String value = provider.mask("Red");
            assertNotNull(value);
            assertThat(value, is(in(terms)));
        }
    }

    @Test
    public void worksForLaplace() throws NumberFormatException {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("differentialPrivacy.mechanism", Mechanism.LAPLACE_NATIVE.name());
        configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
        configuration.setValue("differentialPrivacy.range.min", 0);
        configuration.setValue("differentialPrivacy.range.max", 1);
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);

        int sameValue = 0;
        int outOfBound = 0;
        for (int i = 0; i < 100; ++i) {
            String valueString = provider.mask("0.5");
            double value = Double.parseDouble(valueString);

            if (value == 0.5) {
                sameValue += 1;
            }

            if (value > 1 || value < 0) {
                outOfBound += 1;
            }
        }

        assertThat(sameValue, lessThan(10));
        assertThat(outOfBound, greaterThan(10));
    }

    @Test
    public void worksForLaplaceBounded() throws NumberFormatException {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("differentialPrivacy.mechanism", Mechanism.LAPLACE_BOUNDED.name());
        configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
        configuration.setValue("differentialPrivacy.range.min", 0);
        configuration.setValue("differentialPrivacy.range.max", 1);
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
        String valueString = provider.mask("0.5");
        double value = Double.parseDouble(valueString);

        assertThat(value, lessThanOrEqualTo(1.0));
        assertThat(value, greaterThanOrEqualTo(0.0));
    }

    @Test
    public void worksForLaplaceTruncated() throws NumberFormatException {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("differentialPrivacy.mechanism", Mechanism.LAPLACE_TRUNCATED.name());
        configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
        configuration.setValue("differentialPrivacy.range.min", 0);
        configuration.setValue("differentialPrivacy.range.max", 1);
        DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
        String valueString = provider.mask("0.5");
        double value = Double.parseDouble(valueString);

        assertThat(value, lessThanOrEqualTo(1.0));
        assertThat(value, greaterThanOrEqualTo(0.0));
    }

    @Test
    public void wrongOrderOfBoundsLaplace() throws NumberFormatException {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.mechanism", Mechanism.LAPLACE_NATIVE.name());
            configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
            configuration.setValue("differentialPrivacy.range.min", 100);
            configuration.setValue("differentialPrivacy.range.max", 0);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String valueString = provider.mask("0.5");
            double value = Double.parseDouble(valueString);

            assertThat(value, is(0.5)); // useless, as it should throw earlier
        });
    }

    @Test
    public void nullHierarchyCategorical() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.mechanism", Mechanism.CATEGORICAL.name());
            configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
            configuration.setValue("differentialPrivacy.categorical.set", null);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String value = provider.mask("Red");

            List<String> terms = Arrays.asList("RED", "GREEN", "BLUE");

            assertNotNull(value);
            assertThat(value, is(in(terms)));
        });
    }

    @Test
    public void undefinedBinaryValue() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("differentialPrivacy.mechanism", Mechanism.BINARY.name());
            configuration.setValue("differentialPrivacy.binary.value1", "1");
            configuration.setValue("differentialPrivacy.binary.value2", null);
            configuration.setValue("differentialPrivacy.parameter.epsilon", 0.1);
            DifferentialPrivacyMaskingProvider provider = new DifferentialPrivacyMaskingProvider(configuration);
            String value = provider.mask("1");

            assertNotNull(value);
            assertThat(value, is(in(new String[]{"1", "2"})));
        });
    }
}
