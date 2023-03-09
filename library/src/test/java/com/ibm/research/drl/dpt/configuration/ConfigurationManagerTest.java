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
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationManagerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void loadStream() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);
        }
    }

    @Test
    public void getFieldConfiguration() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);

            MaskingConfiguration configuration = manager.getFieldConfiguration("field2");

            assertNotNull(configuration);

            assertThat(configuration.getBooleanValue("phone.countryCode.preserve"), is(true));
        }
    }

    @Test
    public void getDefaultConfiguration() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);

            MaskingConfiguration configuration = manager.getDefaultConfiguration();

            assertNotNull(configuration);

            assertThat(configuration.getBooleanValue("phone.countryCode.preserve"), is(false));
            
            assertEquals(JsonNodeType.OBJECT, configuration.getJsonNodeValue("generalization.masking.hierarchyMap").getNodeType());
        }
    }
}