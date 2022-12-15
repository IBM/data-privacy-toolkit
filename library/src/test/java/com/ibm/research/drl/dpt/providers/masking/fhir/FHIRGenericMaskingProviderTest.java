/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRGenericMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testBasicMaskWithoutArrays() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/owner:FHIR_Reference");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf, new DefaultMaskingConfiguration());

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        String originalOwnerReference = "Organization/2.16.840.1.113883.19.5";

        assertEquals(originalOwnerReference, device.get("owner").get("reference").asText());

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                maskingConfiguration, new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);

        assertNotEquals(originalOwnerReference, maskedDevice.get("owner").get("reference").asText());
    }

    @Test
    public void testBasicMaskWithArrays() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/owner:FHIR_Reference");
        deviceMaskConf.add("/note:FHIR_Annotation");
        deviceMaskConf.add("/contact:FHIR_ContactPoint");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf, new DefaultMaskingConfiguration());

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        String originalReference = "Practitioner/xcda-author";
        String originalContactValue = "ext 4352";

        assertEquals(originalReference, device.get("note").iterator().next().get("authorReference").get("reference").asText());
        assertEquals(originalContactValue, device.get("contact").iterator().next().get("value").asText());

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                maskingConfiguration, new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);
        assertNotEquals(originalReference, maskedDevice.get("note").iterator().next().get("authorReference").get("reference").asText());
        assertNotEquals(originalContactValue, device.get("contact").iterator().next().get("value").asText());
    }

    @Test
    public void testMaskDelete() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/owner:Reference");
        deviceMaskConf.add("/note/authorReference:Delete");
        deviceMaskConf.add("/contact:Delete");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf, new DefaultMaskingConfiguration());

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        String originalReference = "Practitioner/xcda-author";
        String originalContactValue = "ext 4352";

        assertEquals(originalReference, device.get("note").iterator().next().get("authorReference").get("reference").asText());
        assertEquals(originalContactValue, device.get("contact").iterator().next().get("value").asText());


        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                new DefaultMaskingConfiguration(), new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);

        assertTrue(maskedDevice.get("note").iterator().next().get("authorReference").isNull());
        assertTrue(maskedDevice.get("contact").isNull());
    }


    @Test
    public void testBasicMaskDeleteComplex() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/note:Delete");
        deviceMaskConf.add("/expiry:Delete");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf);

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        assertTrue(device.get("note").isArray());

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                new DefaultMaskingConfiguration(), new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);
        assertTrue(maskedDevice.get("note").isNull());
    }

    @Test
    public void testBasicMaskDeleteSimple() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/note:Delete");
        deviceMaskConf.add("/expiry:Delete");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf);

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        assertTrue(device.get("expiry").isTextual());

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                new DefaultMaskingConfiguration(), new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);
        assertTrue(maskedDevice.get("expiry").isNull());
    }

    @Test
    public void testBasicMaskFieldAbsent() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/url:HASH");
        deviceMaskConf.add("/type:FHIR_CodeableConcept");
        deviceMaskConf.add("/note/authorReference:FHIR_Reference");
        deviceMaskConf.add("/expiry:Delete");
        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf);

        JsonNode device = new ObjectMapper().readTree("{\"resourceType\": \"Device\"}");

        assertNull(device.get("url"));

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                new DefaultMaskingConfiguration(), new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);
    }

    @Test
    public void testBasicMaskSimpleMaskingProvider() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/expiry:HASH");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf);

        JsonNode device = new ObjectMapper().readTree(
                this.getClass().getResourceAsStream("/fhir/deviceExample.json"));

        String originalReference = "2020-08-08";

        assertEquals(originalReference, device.get("expiry").asText());

        FHIRGenericMaskingProvider genericMaskingProvider = new FHIRGenericMaskingProvider(resourceConfiguration,
                new DefaultMaskingConfiguration(), new HashSet<String>(), this.factory);

        JsonNode maskedDevice = genericMaskingProvider.mask(device);
        assertNotEquals(originalReference, maskedDevice.get("expiry").asText());
    }
}


