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
package com.ibm.research.drl.dpt.rest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ExploreTest {
    // year of birth, ZIP code, gender and race of the first patients of the healthcare data set
    private static final int ROWS = 400;
    private static String data;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void loadData() throws Exception {
        try (InputStream in = ExploreTest.class.getResourceAsStream("/healthcare-dataset.txt")) {
            data = Arrays.stream(new String(Objects.requireNonNull(in).readAllBytes(), StandardCharsets.UTF_8).split("\n"))
                    .limit(ROWS)
                    .collect(Collectors.joining("\n"));
        }
    }

    /** The parameters of a request like the one the web UI sends, for year of birth, ZIP code and gender. */
    private Map<String, String> parameters(String minK, String maxK, String kInterval,
                                           String minSuppression, String maxSuppression, String suppressionInterval) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("minK", minK);
        parameters.put("maxK", maxK);
        parameters.put("kInterval", kInterval);
        parameters.put("minSuppression", minSuppression);
        parameters.put("maxSuppression", maxSuppression);
        parameters.put("suppressionInterval", suppressionInterval);
        parameters.put("fields", "{\"Column 4\":{\"name\":\"YOB\"},\"Column 5\":{\"name\":\"ZIPCODE\"},\"Column 6\":{\"name\":\"GENDER\"}}");
        parameters.put("vulnerabilities", "[4,5,6]");
        parameters.put("sensitive", "[]");
        parameters.put("linkingAttributes", "[4,5,6]");
        parameters.put("data", data);
        return parameters;
    }

    private MockHttpServletRequestBuilder request(Map<String, String> parameters) {
        final MockHttpServletRequestBuilder builder = multipart("/api/feature/explore/false");
        parameters.forEach(builder::param);
        return builder;
    }

    private MockHttpServletRequestBuilder request(String minK, String maxK, String kInterval,
                                                  String minSuppression, String maxSuppression, String suppressionInterval) {
        return request(parameters(minK, maxK, kInterval, minSuppression, maxSuppression, suppressionInterval));
    }

    private MockHttpServletRequestBuilder requestWith(String name, String value) {
        final Map<String, String> parameters = parameters("2", "2", "1", "0", "0", "1");
        parameters.put(name, value);
        return request(parameters);
    }

    @Test
    void reportsRiskAndUtilityForEveryPairOfParameters() throws Exception {
        mvc.perform(request("2", "10", "4", "0", "10", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // k = 2, 6, 10 and suppression = 0, 10
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[*].k", containsInAnyOrder(2, 2, 6, 6, 10, 10)))
                .andExpect(jsonPath("$[*].suppression", containsInAnyOrder(0.0, 10.0, 0.0, 10.0, 0.0, 10.0)));
    }

    @Test
    void everyResultHasWhatTheChartNeeds() throws Exception {
        mvc.perform(request("2", "2", "1", "5", "5", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bestNode", containsString("[")))
                .andExpect(jsonPath("$[0].realSuppression", lessThanOrEqualTo(5.0)))
                .andExpect(jsonPath("$[0].realSuppression", greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$[0].informationLoss.NUE.value").isNumber())
                .andExpect(jsonPath("$[0].informationLoss.CP.value").isNumber())
                .andExpect(jsonPath("$[0].informationLoss.AECS.value").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.KRM").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.FKRM").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.BINOM").isNumber());
    }

    @Test
    void riskOfTheKRatioMetricIsBoundedByTheValueOfK() throws Exception {
        final String body = mvc.perform(request("2", "10", "4", "10", "10", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final JsonNode results = new ObjectMapper().readTree(body);
        assertThat(results.size(), equalTo(3));
        for (JsonNode result : results) {
            // the risk of the k-ratio metric is gamma (1.0) over the size of the smallest equivalence class, which
            // is at least k in a k-anonymous dataset
            final double risk = result.get("riskMetricResults").get("KRM").asDouble();
            assertThat(risk, greaterThanOrEqualTo(0.0));
            assertThat(risk, lessThanOrEqualTo(1.0 / result.get("k").asInt()));
        }
    }

    @Test
    void omitsThePairsThatNoGeneralizationSatisfies() throws Exception {
        // 400 records cannot be in classes of 1000: only k = 2 is reported
        mvc.perform(request("2", "1000", "998", "0", "0", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].k").value(2));
    }

    @Test
    void rejectsAMinimumKLargerThanTheDataset() throws Exception {
        // 400 records cannot be in classes of 1000, and nothing is left to explore
        mvc.perform(request("1000", "1000", "1", "0", "0", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("larger than the number of records")));
    }

    @Test
    void rejectsTooManyCombinations() throws Exception {
        mvc.perform(request("2", "100", "1", "0", "20", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvalidParameters() throws Exception {
        mvc.perform(request("1", "10", "1", "0", "10", "1")).andExpect(status().isBadRequest());
        mvc.perform(request("10", "2", "1", "0", "10", "1")).andExpect(status().isBadRequest());
        mvc.perform(request("2", "10", "0", "0", "10", "1")).andExpect(status().isBadRequest());
        mvc.perform(request("2", "10", "1", "-1", "10", "1")).andExpect(status().isBadRequest());
        mvc.perform(request("2", "10", "1", "0", "101", "1")).andExpect(status().isBadRequest());
        mvc.perform(request("2", "10", "1", "0", "10", "0")).andExpect(status().isBadRequest());
    }

    @Test
    void explainsWhyTheParametersAreRejected() throws Exception {
        mvc.perform(request("1", "10", "1", "0", "10", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The minimum k must be at least 2"));
        mvc.perform(request("2", "100", "1", "0", "20", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Too many combinations")));
    }

    @Test
    void rejectsParametersThatAreNotNumbers() throws Exception {
        mvc.perform(request("NaN", "10", "1", "0", "10", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The parameter minK is not a valid number"));
        mvc.perform(request("2", "10", "1", "NaN", "10", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("suppression")));
    }

    @Test
    void rejectsColumnsThatDoNotExist() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "[4,500]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown column 500"));
    }

    @Test
    void needsAtLeastOneQuasiIdentifier() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("At least one quasi-identifier is needed"));
    }

    @Test
    void acceptsTheLinkingAttributesTheWebUiSendsAfterAColumnIsToggled() throws Exception {
        mvc.perform(requestWith("linkingAttributes", "[4,6]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void rejectsLinkingAttributesThatAreNotAList() throws Exception {
        // what JSON.stringify makes of a Set, which is what the web UI used to send once a column was toggled
        mvc.perform(requestWith("linkingAttributes", "{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    @Test
    void rejectsTypesThatAreNotKnown() throws Exception {
        mvc.perform(requestWith("fields", "{\"Column 4\":{\"name\":\"NOT_A_TYPE\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown type for the column Column 4"));
    }

    @Test
    void rejectsTypesWithoutAName() throws Exception {
        mvc.perform(requestWith("fields", "{\"Column 4\":{}}"))
                .andExpect(status().isBadRequest());
        mvc.perform(requestWith("fields", "{\"Column 4\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doesNotRevealDetailsOfTheDataOrTheServerInErrors() throws Exception {
        final String body = mvc.perform(requestWith("vulnerabilities", "[4,500]"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertThat(body, equalTo("{\"error\":\"Unknown column 500\"}"));
    }

    @Test
    void rejectsMalformedColumnListsWithoutRevealingTheParser() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("{\"error\":\"Invalid request\"}")));
    }
}
