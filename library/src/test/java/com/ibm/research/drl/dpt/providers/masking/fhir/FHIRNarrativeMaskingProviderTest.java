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
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRNarrativeMaskingProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FHIRNarrativeMaskingProviderTest {

    @Test
    public void testBasic() throws Exception {
        String json = "{\n" +
                "              \"status\" : \"1.2\"," +
                "              \"div\": \"divcontents\"\n" +
                "            }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRNarrative narrative = objectMapper.readValue(json, FHIRNarrative.class);

        assertEquals("divcontents", narrative.getDiv());

        FHIRNarrativeMaskingProvider narrativeMaskingProvider = new FHIRNarrativeMaskingProvider(new DefaultMaskingConfiguration());
        FHIRNarrative maskedNarrative = narrativeMaskingProvider.mask(narrative);

        assertNull(maskedNarrative.getDiv());
    }
}


