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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRHumanName;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRHumanNameMaskingProvider;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRHumanNameMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testBasic() throws Exception {
        String nameJson = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   Peter James Chalmers, but called \\\"Jim\\\"   \"\n" +
                "      ],\n" +
                "      \"use\": \"official\",\n" +
                "      \"family\": [\n" +
                "        \"Chalmers\"\n" +
                "      ],\n" +
                "      \"given\": [\n" +
                "        \"Peter\",\n" +
                "        \"James\"\n" +
                "      ]\n" +
                "    }";

        ObjectMapper objectMapper = JsonUtils.MAPPER;
       
        int fnamesOK = 0;
        int gnamesOK = 0;
        
        for(int i = 0; i < 1000; i++) {
            FHIRHumanName name = objectMapper.readValue(nameJson, FHIRHumanName.class);

            ArrayList<String> familyNames = new ArrayList<>(name.getFamily());

            ArrayList<String> givenNames = new ArrayList<>(name.getGiven());

            FHIRHumanNameMaskingProvider maskingProvider = new FHIRHumanNameMaskingProvider(
                    new DefaultMaskingConfiguration(), new HashSet<>(), "/name", this.factory);
            FHIRHumanName maskedName = maskingProvider.mask(name);

            assertEquals(maskedName.getFamily().size(), familyNames.size());
            assertEquals(maskedName.getGiven().size(), givenNames.size());

            for (String fname : familyNames) {
                if (!maskedName.getFamily().contains(fname)) {
                    fnamesOK++;
                }
            }

            for (String gname : givenNames) {
                if(!maskedName.getGiven().contains(gname)) {
                    gnamesOK++;
                }
            }
        }
        
        assertTrue(gnamesOK > 0);
        assertTrue(fnamesOK > 0);

    }

    @Test
    public void testGivenAbsent() throws Exception {
        String nameJson = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   Peter James Chalmers, but called \\\"Jim\\\"   \"\n" +
                "      ],\n" +
                "      \"use\": \"official\",\n" +
                "      \"family\": [\n" +
                "        \"Chalmers\"\n" +
                "      ]\n" +
                "    }";
        
        int fnamesOK = 0;
        
        for(int i =0; i < 1000; i++) {
            FHIRHumanName name = JsonUtils.MAPPER.readValue(nameJson, FHIRHumanName.class);

            List<String> familyNames = new ArrayList<>(name.getFamily());

            FHIRHumanNameMaskingProvider maskingProvider = new FHIRHumanNameMaskingProvider(
                    new DefaultMaskingConfiguration(), new HashSet<String>(), "/name", this.factory);
            FHIRHumanName maskedName = maskingProvider.mask(name);

            assertEquals(maskedName.getFamily().size(), familyNames.size());

            for (String fname : familyNames) {
                if(!maskedName.getFamily().contains(fname)) {
                    fnamesOK++;
                }
            }

            assertNull(maskedName.getGiven());
        }
        
        assertTrue(fnamesOK > 0);
    }

    @Test
    public void testAllAbsent() throws Exception {
        String nameJson = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   Peter James Chalmers, but called \\\"Jim\\\"   \"\n" +
                "      ],\n" +
                "      \"use\": \"official\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRHumanName name = objectMapper.readValue(nameJson, FHIRHumanName.class);

        FHIRHumanNameMaskingProvider maskingProvider = new FHIRHumanNameMaskingProvider(
                new DefaultMaskingConfiguration(), new HashSet<String>(), "/name", this.factory);
        FHIRHumanName maskedName = maskingProvider.mask(name);

        assertNull(maskedName.getGiven());
        assertNull(maskedName.getFamily());
    }

    @Test
    public void testFamilyAbsent() throws Exception {
        String nameJson = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   Peter James Chalmers, but called \\\"Jim\\\"   \"\n" +
                "      ],\n" +
                "      \"use\": \"official\",\n" +
                "      \"given\": [\n" +
                "        \"Peter\",\n" +
                "        \"James\"\n" +
                "      ]\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRHumanName name = objectMapper.readValue(nameJson, FHIRHumanName.class);

        List<String> givenNames = new ArrayList<>(name.getGiven());

        FHIRHumanNameMaskingProvider maskingProvider = new FHIRHumanNameMaskingProvider(
                new DefaultMaskingConfiguration(), new HashSet<>(), "/name", this.factory);
        FHIRHumanName maskedName = maskingProvider.mask(name);

        assertEquals(maskedName.getGiven().size(), givenNames.size());

        assertNull(maskedName.getFamily());

        int matched = 0;
        for(String gname: givenNames) {
            if (maskedName.getGiven().contains(gname)) matched += 1;
        }

        assertNotEquals(givenNames.size(), matched);
    }


    @Test
    public void testRemoveExtensions() throws Exception {
        String nameJson = "{\n" +
                "  \"extension\": [\n" +
                "      {\n" +
                "        \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
                "        \"valueDateTime\": \"1974-12-25T14:35:45-05:00\"\n" +
                "      }\n" +
                "    ]," +
                "    \"fhir_comments\": [\n" +
                "        \"   Peter James Chalmers, but called \\\"Jim\\\"   \"\n" +
                "      ],\n" +
                "      \"use\": \"official\",\n" +
                "      \"family\": [\n" +
                "        \"Chalmers\"\n" +
                "      ],\n" +
                "      \"given\": [\n" +
                "        \"Peter\",\n" +
                "        \"James\"\n" +
                "      ]\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRHumanName name = objectMapper.readValue(nameJson, FHIRHumanName.class);

        assertEquals(name.getExtension().size(), 1);

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.humanName.removeExtensions", true);

        FHIRHumanNameMaskingProvider maskingProvider = new FHIRHumanNameMaskingProvider(
                maskingConfiguration, new HashSet<>(), "/name", this.factory);
        FHIRHumanName maskedName = maskingProvider.mask(name);

        assertNull(maskedName.getExtension());
    }
}

