/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
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

