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
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneralizationMaskingProviderTest {
    @Test
    public void testGeneralizationGender() throws Exception {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("generalization.masking.hierarchyLevel", 1);
        maskingConfiguration.setValue("generalization.masking.hierarchyName", "gender");

        JsonNode hierarchyMapNode = JsonUtils.MAPPER.readTree("{\"gender\": \"GENDER\"}");
        maskingConfiguration.setValue("generalization.masking.hierarchyMap", hierarchyMapNode);

        MaskingProvider maskingProvider = new GeneralizationMaskingProvider(
                new SecureRandom(), maskingConfiguration);

        String originalValue = "Male";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("*", maskedValue);
    }

    @Test
    public void testGeneralizationGenderLevel0() throws Exception {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("generalization.masking.hierarchyLevel", 0);
        maskingConfiguration.setValue("generalization.masking.hierarchyName", "gender");

        JsonNode hierarchyMapNode = JsonUtils.MAPPER.readTree("{\"gender\": \"GENDER\"}");
        maskingConfiguration.setValue("generalization.masking.hierarchyMap", hierarchyMapNode);

        MaskingProvider maskingProvider = new GeneralizationMaskingProvider(new SecureRandom(), maskingConfiguration);

        String originalValue = "Male";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals(maskedValue.toUpperCase(), originalValue.toUpperCase());
    }
}