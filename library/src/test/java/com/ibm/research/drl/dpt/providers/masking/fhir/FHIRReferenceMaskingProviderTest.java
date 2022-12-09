/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRReferenceMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRReferenceMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory();

    @Test
    public void testBasic() throws  Exception {
        String json = "{\n" +
                "    \"reference\": \"Organization/2.16.840.1.113883.19.5\", \n" +
                "    \"display\": \"Central Supply\"" +
                "}";

        ObjectMapper objectMapper = FHIRMaskingUtils.getObjectMapper();
        FHIRReference reference = objectMapper.readValue(json, FHIRReference.class);

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

        ObjectMapper objectMapper = FHIRMaskingUtils.getObjectMapper();
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

        ObjectMapper objectMapper = FHIRMaskingUtils.getObjectMapper();
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

        ObjectMapper objectMapper = FHIRMaskingUtils.getObjectMapper();
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

        ObjectMapper objectMapper = FHIRMaskingUtils.getObjectMapper();
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


