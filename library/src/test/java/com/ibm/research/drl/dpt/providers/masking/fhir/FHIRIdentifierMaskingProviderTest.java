/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRIdentifierMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRIdentifierMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testURN() throws Exception{
        String identifierJSON = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"value\": \"urn:uuid:a76d9bbf-f293-4fb7-ad4c-2851cac77162\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        String originalValue = identifier.getValue();

        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(new DefaultMaskingConfiguration(), new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        String maskedValue = maskedIdentifier.getValue();

        assertNotEquals(maskedValue, originalValue);
        assertEquals(maskedValue.length(), originalValue.length());
    }

    @Test
    public void testValueMissing() throws Exception{
        String identifierJSON = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        String originalValue = identifier.getValue();
        assertNull(originalValue);

        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(new DefaultMaskingConfiguration(), new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        String maskedValue = maskedIdentifier.getValue();
        assertNull(maskedValue);
    }

    @Test
    public void testURL() throws Exception {
        String identifierJSON = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"value\": \"http://www.nba.com\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        String originalValue = identifier.getValue();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.contactPoint.removeSystem", false);
        maskingConfiguration.setValue("default.masking.provider", "URL");

        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(maskingConfiguration, new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        String maskedValue = maskedIdentifier.getValue();

        assertNotEquals(maskedValue, originalValue);
        assertTrue(new URLIdentifier().isOfThisType(maskedValue));
    }

    @Test
    public void testRandom() throws Exception {
        String identifierJSON = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"value\": \"12345\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        String originalValue = identifier.getValue();

        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(new DefaultMaskingConfiguration(), new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        String maskedValue = maskedIdentifier.getValue();

        assertNotEquals(maskedValue, originalValue);
        assertEquals(maskedValue.length(), originalValue.length());
    }

    @Test
    public void testOtherDefaultProvider() throws Exception {
        String identifierJSON = "{\n" +
                "      \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"value\": \"12345678\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        String originalValue = identifier.getValue();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("default.masking.provider", ProviderType.REPLACE.name());
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(maskingConfiguration, new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        String maskedValue = maskedIdentifier.getValue();

        assertNotEquals(maskedValue, originalValue);
        assertEquals("********", maskedValue);
    }

    @Test
    public void testExtensionsRemoved() throws Exception {
        String identifierJSON = "{\n" +
                "    \"extension\": [\n" +
                "    {\n" +
                "      \"url\": \"http://hl7.org/fhir/StructureDefinition/us-core-race\",\n" +
                "      \"valueCodeableConcept\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v3/Race\",\n" +
                "            \"code\": \"1096-7\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    }],  " +
                "    \"fhir_comments\": [\n" +
                "        \"   MRN assigned by ACME healthcare on 6-May 2001   \"\n" +
                "      ],\n" +
                "      \"use\": \"usual\",\n" +
                "      \"type\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
                "            \"code\": \"MR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                "      \"value\": \"12345\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2001-05-06\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Acme Healthcare\"\n" +
                "      }\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRIdentifier identifier = objectMapper.readValue(identifierJSON, FHIRIdentifier.class);

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.identifier.removeExtensions", true);

        FHIRIdentifierMaskingProvider maskingProvider = new FHIRIdentifierMaskingProvider(maskingConfiguration, new HashSet<String>(), "/foo", this.factory);
        FHIRIdentifier maskedIdentifier = maskingProvider.mask(identifier);

        assertNull(maskedIdentifier.getExtension());
    }
}


