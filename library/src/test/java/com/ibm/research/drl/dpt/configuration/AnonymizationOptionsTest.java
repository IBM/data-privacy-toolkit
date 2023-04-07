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
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.DummyHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.CategoricalPrecision;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.risk.KRatioMetric;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnonymizationOptionsTest {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    @Test
    public void testPrivacyConstraintsNegativeK() {
        assertThrows(MisconfigurationException.class, () -> AnonymizationOptions.privacyConstraintsFromJSON(
                OBJECT_MAPPER.readTree(
                "[{\"name\": \"k\", \"k\": -2}]"
        )));
    }

    @Test
    public void testPrivacyConstraintsKEqualsToOne() {
        assertThrows(MisconfigurationException.class, () -> AnonymizationOptions.privacyConstraintsFromJSON(
                OBJECT_MAPPER.readTree(
                "[{\"name\": \"k\", \"k\": 1}]"
        )));
    }

    @Test
    public void testPrivacyConstraintsNotAnArray() {
        assertThrows(MisconfigurationException.class, () -> AnonymizationOptions.privacyConstraintsFromJSON(
                OBJECT_MAPPER.readTree(
                "{\"name\": \"k\", \"k\": 2}"
        )));
    }

    @Test
    public void testPrivacyConstraintsUnknownName() {
        assertThrows(MisconfigurationException.class, () -> AnonymizationOptions.privacyConstraintsFromJSON(
                OBJECT_MAPPER.readTree(
                "{\"name\": \"foobar\", \"k\": 2}"
        )));
    }

    @Test
    public void testPrivacyConstraintsLEqualsToZero() {
        assertThrows(MisconfigurationException.class, () -> AnonymizationOptions.privacyConstraintsFromJSON(
                OBJECT_MAPPER.readTree(
                "[{\"name\": \"distinctL\", \"l\": 0}]"
        )));
    }

    @Test
    public void testValid() throws Exception {
        try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsValid.json")) {
            AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
            assertNotNull(anonymizationOptions);
        }
    }

    @Test
    public void testWeightNotNumeric() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsWeightNotNumeric.json")) {

                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testWeightNegative() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsWeightNegative.json")) {

                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testMaximumLevelNotNumeric() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsMaximumLevelNotNumeric.json")) {

                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testMaximumLevelInvalid() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsMaximumLevelInvalid.json")) {

                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testInvalidColumnType() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsInvalidColumnType.json")) {

                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testMissingColumnType() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsMissingColumnType.json")) {
                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testMissingHierarchy() {
        assertThrows(Exception.class, () -> {
            try (InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsMissingHierarchy.json")) {
                AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
                assertNotNull(anonymizationOptions);
            }
        });
    }

    @Test
    public void testUndefinedHierarchyName() {
        assertThrows(Exception.class, () -> {
            InputStream inputStream = AnonymizationOptionsTest.class.getResourceAsStream("/anonymizationOptionsUndefinedHierarchyName.json");
            AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue(inputStream, AnonymizationOptions.class);
            assertNotNull(anonymizationOptions);
        });
    }

    @Test
    public void failsForHierarchiesFieldDelimiter() {
        assertThrows(Exception.class, () -> {
            AnonymizationOptions anonymizationOptions = OBJECT_MAPPER.readValue("{}", AnonymizationOptions.class);
            assertNotNull(anonymizationOptions);
        });
    }

    @Test
    public void wellFormattedAndCompleteConfiguration() throws Exception {
        AnonymizationOptions options = OBJECT_MAPPER.readValue("{" +
                "\"delimiter\": \",\"," +
                "\"quoteChar\": \",\"," +
                "\"hasHeader\": false," +
                "\"trimFields\": false," +
                "\"hierarchies\": {}," +
                "\"privacyConstraints\": []," +
                "\"columnInformation\": []," +
                "\"estimateUniqueness\": true," +
                "\"informationLoss\": \"CP\"," +
                "\"riskMetric\": \"KRM\"," +
                "\"riskMetricOptions\": {\"gamma\": \"0.1\"}" +
                "}", AnonymizationOptions.class);
        assertNotNull(options);

        assertThat(options.getRiskMetric().getShortName(), is(new KRatioMetric().getShortName()));
    }

    @Test
    public void testHierarchyFromObject() throws Exception {
        AnonymizationOptions options = OBJECT_MAPPER.readValue("{" +
                "\"delimiter\": \",\"," +
                "\"quoteChar\": \"\\\"\"," +
                "\"hasHeader\": false," +
                "\"trimFields\": false," +
                "\"hierarchies\": {" +
                "\"dummy\":{\"className\":\"" + DummyHierarchy.class.getCanonicalName() +"\",\"options\": {}}" +
                "}," +
                "\"privacyConstraints\": []," +
                "\"columnInformation\": []," +
                "\"estimateUniqueness\": true," +
                "\"informationLoss\": \"CP\"," +
                "\"riskMetric\": null," +
                "\"riskMetricOptions\": {\"gamma\":0.1}" +
                "}",  AnonymizationOptions.class);

        assertNotNull(options);

        assertThat(options.getInformationLoss().getShortName(), is(new CategoricalPrecision().getShortName()));
    }
}
