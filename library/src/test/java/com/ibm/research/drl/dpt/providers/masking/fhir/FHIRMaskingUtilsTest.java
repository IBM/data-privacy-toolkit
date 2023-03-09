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
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRMaskingUtilsTest {

    @Test
    public void testPreprocessing() throws Exception {
        String cpJSON = "{\n" +
                "      \"resourceType\" : \"ContactPoint\", " +
                "      \"system\": \"email\",\n" +
                "      \"value\": \"p.heuvel@gmail.com\",\n" +
                "      \"use\": \"home\"\n" +
                "    }";

        String processed = FHIRMaskingUtils.preprocessFHIRObject(cpJSON);
        System.out.println(processed);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(processed);

        assertNotNull(node.get("/fhir/ContactPoint"));
        assertEquals(4, node.get("/fhir/ContactPoint").size());
    }

    @Test
    public void testPostprocessing() throws IOException {
        String json = "{\"/fhir/ContactPoint\":{\"resourceType\":\"ContactPoint\",\"system\":\"email\",\"value\":\"p.heuvel@gmail.com\",\"use\":\"home\"}}";

        String processed = FHIRMaskingUtils.postProcessFHIRObject(json);
        System.out.println(processed);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(processed);

        assertEquals(4, node.size());
        assertEquals("ContactPoint", node.get("resourceType").asText());
    }

    @Test
    public void testPreprocessingInvalidObject() throws Exception {
        /* resourceType is missing so preprocessing should return null */
        String cpJSON = "{\n" +
                "      \"system\": \"email\",\n" +
                "      \"value\": \"p.heuvel@gmail.com\",\n" +
                "      \"use\": \"home\"\n" +
                "    }";

        String processed = FHIRMaskingUtils.preprocessFHIRObject(cpJSON);
        assertNull(processed);
    }
}


