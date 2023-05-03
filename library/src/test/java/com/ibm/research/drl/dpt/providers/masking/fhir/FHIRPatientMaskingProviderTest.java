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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRHumanName;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRPatient;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRPatientMaskingProviderTest {

    private String getFileContents(InputStream inputStream) throws IOException {
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {

            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }

    }

    @Test
    public void testMapping() throws IOException {
        String contents;
        try (InputStream inputStream = FHIRPatientMaskingProviderTest.class.getResourceAsStream("/fhir/patientExample.json");) {
            contents = getFileContents(inputStream);
        }

        FHIRPatient fhirPatient = JsonUtils.MAPPER.readValue(contents, FHIRPatient.class);

        Collection<FHIRHumanName> names = fhirPatient.getName();

        assertEquals(2, names.size());

        FHIRHumanName name1 = names.iterator().next();
        assertEquals("official", name1.getUse());
        assertEquals(1, name1.getFamily().size());
        assertEquals("Chalmers", name1.getFamily().iterator().next());
        assertEquals(2, name1.getGiven().size());

        StringWriter stringWriter = new StringWriter();
        JsonUtils.MAPPER.writeValue(stringWriter, fhirPatient);

        String jsonString = stringWriter.toString();
        JsonNode node = JsonUtils.MAPPER.readTree(jsonString);

        assertEquals("patient", node.get("resourceType").asText().toLowerCase());
    }


}


