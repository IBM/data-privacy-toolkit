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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRReferenceMaskingProvider;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRReferenceMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testBasic() throws  Exception {
        String json = "{\n" +
                "    \"reference\": \"Organization/2.16.840.1.113883.19.5\", \n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        FHIRReference reference = JsonUtils.MAPPER.readValue(json, FHIRReference.class);

        assertEquals("Central Supply", reference.getDisplay());
        assertEquals("Organization/2.16.840.1.113883.19.5", reference.getReference());

        String originalReference = reference.getReference();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);
        maskingConfiguration.setValue("fhir.reference.removeDisplay", true);

        FHIRReferenceMaskingProvider maskingProvider = new FHIRReferenceMaskingProvider(maskingConfiguration, new HashSet<String>(), "/", this.factory);
        FHIRReference maskedReference = maskingProvider.mask(reference);

        assertNull(maskedReference.getDisplay());
        assertNotEquals(maskedReference.getReference(), originalReference);
    }

    @Test
    public void testExcludePrefix() throws  Exception {
        String json = "{\n" +
                "    \"reference\": \"Organization/2.16.840.1.113883.19.5\", \n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        String organizationValue = "Organization/2.16.840.1.113883.19.5";

        ObjectMapper objectMapper = JsonUtils.MAPPER;
        FHIRReference reference = objectMapper.readValue(json, FHIRReference.class);

        assertEquals("Central Supply", reference.getDisplay());
        assertEquals(reference.getReference(), organizationValue);

        String originalReference = reference.getReference();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);
        maskingConfiguration.setValue("fhir.reference.maskReferenceExcludePrefixList", "Organization,Patient");

        FHIRReferenceMaskingProvider maskingProvider = new FHIRReferenceMaskingProvider(maskingConfiguration, new HashSet<String>(), "/", this.factory);
        FHIRReference maskedReference = maskingProvider.mask(reference);

        assertEquals(maskedReference.getReference(), originalReference);
    }

    @Test
    public void testExcludePrefixSetOK() throws  Exception {
        String json = "{\n" +
                "    \"reference\": \"Organization/2.16.840.1.113883.19.5\", \n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        String organizationValue = "Organization/2.16.840.1.113883.19.5";

        ObjectMapper objectMapper = JsonUtils.MAPPER;
        FHIRReference reference = objectMapper.readValue(json, FHIRReference.class);

        assertEquals("Central Supply", reference.getDisplay());
        assertEquals(reference.getReference(), organizationValue);

        String originalReference = reference.getReference();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);
        maskingConfiguration.setValue("fhir.reference.maskReferenceExcludePrefixList", "Medication,Organization,Patient");

        FHIRReferenceMaskingProvider maskingProvider = new FHIRReferenceMaskingProvider(maskingConfiguration, new HashSet<String>(), "/", this.factory);
        FHIRReference maskedReference = maskingProvider.mask(reference);

        assertEquals(maskedReference.getReference(), originalReference);
    }

    @Test
    public void testExcludePrefixIgnoresCase() throws  Exception {
        String json = "{\n" +
                "    \"reference\": \"Organization/2.16.840.1.113883.19.5\", \n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        String organizationValue = "OrgAnization/2.16.840.1.113883.19.5";

        ObjectMapper objectMapper = JsonUtils.MAPPER;
        FHIRReference reference = objectMapper.readValue(json, FHIRReference.class);

        assertEquals("Central Supply", reference.getDisplay());
        assertEquals(reference.getReference().toUpperCase(), organizationValue.toUpperCase());

        String originalReference = reference.getReference();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);
        maskingConfiguration.setValue("fhir.reference.maskReferenceExcludePrefixList", "OrganiZATion,Patient");

        FHIRReferenceMaskingProvider maskingProvider = new FHIRReferenceMaskingProvider(maskingConfiguration, new HashSet<String>(), "/", this.factory);
        FHIRReference maskedReference = maskingProvider.mask(reference);

        assertEquals(maskedReference.getReference(), originalReference);
    }

    @Test
    public void testBasicReferenceMissing() throws  Exception {
        String json = "{\n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        ObjectMapper objectMapper = JsonUtils.MAPPER;
        FHIRReference reference = objectMapper.readValue(json, FHIRReference.class);

        assertEquals("Central Supply", reference.getDisplay());
        assertNull(reference.getReference());


        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.reference.removeReference", false);
        maskingConfiguration.setValue("fhir.reference.removeDisplay", true);

        FHIRReferenceMaskingProvider maskingProvider = new FHIRReferenceMaskingProvider(maskingConfiguration, new HashSet<String>(), "/", this.factory);
        FHIRReference maskedReference = maskingProvider.mask(reference);

        assertNull(maskedReference.getDisplay());
        assertNull(maskedReference.getReference());
    }
}


