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


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class IdentifyTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testHappyPathWithoutColumnHeaderNoSizeLimit() throws Exception {
        String data;

        try (InputStream inputStream = IdentifyTest.class.getResourceAsStream("/healthcare-dataset.txt")) {
            data = new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
        }

        MvcResult response = mvc.perform(post("/api/feature/identify/false").contentType(MediaType.TEXT_PLAIN).content(data))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String out = response.getResponse().getContentAsString();

        System.out.println(out);
    }

    @Test
    public void testHappyPathWithoutColumnHeaderWithSizeLimit() throws Exception {
        String data;

        try (InputStream inputStream = IdentifyTest.class.getResourceAsStream("/healthcare-dataset.txt")) {
            data = new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
        }

        MvcResult response = mvc.perform(post("/api/feature/identify/false").param("sampleSize", "10").contentType(MediaType.TEXT_PLAIN).content(data))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String out = response.getResponse().getContentAsString();

        System.out.println(out);
    }
}