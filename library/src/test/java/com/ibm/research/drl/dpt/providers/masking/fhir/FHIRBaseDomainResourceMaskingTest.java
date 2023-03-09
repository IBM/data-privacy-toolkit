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
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FHIRBaseDomainResourceMaskingTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testIDPrefixPreserver() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.resource.preserveIdPrefix", true);

        FHIRBaseDomainResourceMaskingProvider maskingProvider = new FHIRBaseDomainResourceMaskingProvider(
                maskingConfiguration, new HashSet<String>(), "/foo", this.factory);

        String resource = "{\"id\": \"Patient/1234\", \"resourceType\": \"Patient\"}";
        JsonNode node = new ObjectMapper().readTree(resource);

        JsonNode maskedResource = maskingProvider.mask(node);

        assertTrue(maskedResource.get("id").asText().startsWith("Patient/"));
    }


    @Test
    public void testIDPrefixNoPreserver() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.resource.preserveIdPrefix", false);

        FHIRBaseDomainResourceMaskingProvider maskingProvider = new FHIRBaseDomainResourceMaskingProvider(
                maskingConfiguration, new HashSet<String>(), "/foo", this.factory);

        String resource = "{\"id\": \"Patient/1234\", \"resourceType\": \"Patient\"}";
        JsonNode node = new ObjectMapper().readTree(resource);

        JsonNode maskedResource = maskingProvider.mask(node);

        assertFalse(maskedResource.get("id").asText().startsWith("Patient/"));
    }
}

