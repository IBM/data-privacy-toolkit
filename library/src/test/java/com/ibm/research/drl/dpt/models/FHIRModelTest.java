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
package com.ibm.research.drl.dpt.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRDevice;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRModelTest {

    @Test
    public void testSerialization() throws Exception{
        InputStream inputStream = this.getClass().getResourceAsStream("/fhir/deviceExample.json");

        ObjectMapper objectMapper = new ObjectMapper();

        FHIRDevice device = objectMapper.readValue(inputStream, FHIRDevice.class);

        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, device);

        String jsonBack = stringWriter.toString();

        System.out.println(jsonBack);

        JsonNode node = objectMapper.readTree(jsonBack);
        assertEquals("device", node.get("resourceType").asText().toLowerCase());
    }
}
