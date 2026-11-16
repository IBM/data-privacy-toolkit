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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The limits that keep one caller from monopolizing the CPU of the service. */
class ExploreLimitsTest {
    private static final String DATA = "1980,12345,M\n1981,12345,F\n1982,12346,M\n1983,12346,F";

    private static MockHttpServletRequestBuilder request() {
        return request("[0,1,2]");
    }

    private static MockHttpServletRequestBuilder request(String vulnerabilities) {
        return multipart("/api/feature/explore/false")
                .param("minK", "2").param("maxK", "2").param("kInterval", "1")
                .param("minSuppression", "0").param("maxSuppression", "0").param("suppressionInterval", "1")
                .param("fields", "{}")
                .param("vulnerabilities", vulnerabilities)
                .param("data", DATA);
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "demo.explore.max-concurrent=0")
    @AutoConfigureMockMvc
    class WhenAllTheSlotsAreTaken {
        @Autowired
        private MockMvc mvc;

        @Test
        void turnsTheRequestAwayAndSaysWhy() throws Exception {
            mvc.perform(request())
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.error", containsString("busy")));
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "demo.explore.timeout-seconds=0")
    @AutoConfigureMockMvc
    class WhenTheExplorationTakesTooLong {
        @Autowired
        private MockMvc mvc;

        @Test
        void givesUpAndSaysWhy() throws Exception {
            mvc.perform(request())
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.error", containsString("more time than allowed")));
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "demo.explore.max-concurrent=1")
    @AutoConfigureMockMvc
    class WhenTheSlotIsReleased {
        @Autowired
        private MockMvc mvc;

        @Test
        void servesTheRequestsOneAfterTheOther() throws Exception {
            // a request that fails after taking the slot must not keep it
            mvc.perform(request("[]")).andExpect(status().isBadRequest());
            mvc.perform(request("[]")).andExpect(status().isBadRequest());
            mvc.perform(request()).andExpect(status().isOk());
        }
    }
}
