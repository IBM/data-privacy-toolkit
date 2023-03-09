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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRCodingMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FHIRCodingMaskingProviderTest {

    @Test
    public void testBasic() throws Exception {
        String json = "{\n" +
                "              \"system\": \"http://hl7.org/fhir/care-plan-activity-category\",\n" +
                "              \"version\" : \"1.2\"," +
                "              \"code\": \"observation\"\n" +
                "            }";

        ObjectMapper objectMapper = new ObjectMapper();

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            FHIRCoding coding = objectMapper.readValue(json, FHIRCoding.class);
            assertEquals("1.2", coding.getVersion());
            FHIRCodingMaskingProvider codingMaskingProvider = new FHIRCodingMaskingProvider(new DefaultMaskingConfiguration(),
                    new HashSet<String>(), "/foo", new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
            FHIRCoding maskedCoding = codingMaskingProvider.mask(coding);

            if(!maskedCoding.getVersion().equals("1.2")) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }
}


