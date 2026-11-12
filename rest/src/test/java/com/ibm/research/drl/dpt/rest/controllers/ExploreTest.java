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

import static org.hamcrest.Matchers.containsInAnyOrder;
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
                .andExpect(jsonPath("$[0].informationLoss.NUE.first").isNumber())
                .andExpect(jsonPath("$[0].informationLoss.CP.first").isNumber())
                .andExpect(jsonPath("$[0].informationLoss.AECS.first").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.KRM").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.FKRM").isNumber())
                .andExpect(jsonPath("$[0].riskMetricResults.BINOM").isNumber());
    }

    @Test
    void largerKNeverMeansMoreRisk() throws Exception {
        mvc.perform(request("2", "10", "8", "10", "10", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                // risk of the k-ratio metric is gamma / size of the smallest equivalence class
                .andExpect(jsonPath("$[0].riskMetricResults.KRM", greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$[0].riskMetricResults.KRM", lessThanOrEqualTo(0.5)))
                .andExpect(jsonPath("$[1].riskMetricResults.KRM", lessThanOrEqualTo(0.1)));
    }

    @Test
    void omitsThePairsThatNoGeneralizationSatisfies() throws Exception {
        // 400 records cannot be in classes of 1000
        mvc.perform(request("1000", "1000", "1", "0", "0", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
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
    void rejectsColumnsThatDoNotExist() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "[4,500]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void needsAtLeastOneQuasiIdentifier() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doesNotRevealDetailsOfTheDataInErrors() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "[4,500]"))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("Sophia"))));
    }

    @Test
    void rejectsMalformedColumnLists() throws Exception {
        mvc.perform(requestWith("vulnerabilities", "not json"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(not(containsString("Jackson"))))
                .andExpect(content().string(not(containsString("Unrecognized"))));
    }
}
