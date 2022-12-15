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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.PhoneIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRContactPointMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRContactPointMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testEmail() throws Exception {
        String cpJSON = "{\n" +
                "      \"system\": \"email\",\n" +
                "      \"value\": \"p.heuvel@gmail.com\",\n" +
                "      \"use\": \"home\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRContactPoint contactPoint = objectMapper.readValue(cpJSON, FHIRContactPoint.class);

        String value = contactPoint.getValue();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.contactPoint.removeSystem", false);

        FHIRContactPointMaskingProvider contactPointMaskingProvider = new FHIRContactPointMaskingProvider(
                maskingConfiguration, new HashSet<String>(), "/contact", this.factory);
        FHIRContactPoint maskedContactPoint = contactPointMaskingProvider.mask(contactPoint);

        String maskedValue = maskedContactPoint.getValue();

        assertNotEquals(maskedValue, value);
        assertEquals("email", maskedContactPoint.getSystem());
        assertEquals("home", maskedContactPoint.getUse());
        assertTrue(new EmailIdentifier().isOfThisType(maskedValue));
    }

    @Test
    public void testOther() throws Exception {
        String cpJSON = "{\n" +
                "      \"system\": \"other\",\n" +
                "      \"value\": \"http://www.google.com\",\n" +
                "      \"use\": \"mobile\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRContactPoint contactPoint = objectMapper.readValue(cpJSON, FHIRContactPoint.class);

        String value = contactPoint.getValue();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.contactPoint.removeSystem", false);

        FHIRContactPointMaskingProvider contactPointMaskingProvider =
                new FHIRContactPointMaskingProvider(maskingConfiguration, new HashSet<String>(), "/telecom", this.factory);
        FHIRContactPoint maskedContactPoint = contactPointMaskingProvider.mask(contactPoint);

        String maskedValue = maskedContactPoint.getValue();

        assertNotEquals(maskedValue, value);
        assertEquals("other", maskedContactPoint.getSystem());
        assertEquals("mobile", maskedContactPoint.getUse());
        assertTrue(new URLIdentifier().isOfThisType(maskedValue));
    }

    @Test
    public void testPhone() throws Exception {
        String cpJSON = "{\n" +
                "      \"system\": \"phone\",\n" +
                "      \"value\": \"0648352638\",\n" +
                "      \"use\": \"mobile\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRContactPoint contactPoint = objectMapper.readValue(cpJSON, FHIRContactPoint.class);

        String value = contactPoint.getValue();

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.contactPoint.removeSystem", false);

        FHIRContactPointMaskingProvider contactPointMaskingProvider =
                new FHIRContactPointMaskingProvider(maskingConfiguration, new HashSet<String>(), "/telecom", this.factory);
        FHIRContactPoint maskedContactPoint = contactPointMaskingProvider.mask(contactPoint);

        String maskedValue = maskedContactPoint.getValue();

        assertNotEquals(maskedValue, value);
        assertEquals("phone", maskedContactPoint.getSystem());
        assertEquals("mobile", maskedContactPoint.getUse());
        assertTrue(new PhoneIdentifier().isOfThisType(maskedValue));
    }

    @Test
    public void testValueAbsent() throws Exception {
        String cpJSON = "{\n" +
                "      \"system\": \"phone\",\n" +
                "      \"use\": \"mobile\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRContactPoint contactPoint = objectMapper.readValue(cpJSON, FHIRContactPoint.class);

        String value = contactPoint.getValue();

        FHIRContactPointMaskingProvider contactPointMaskingProvider =
                new FHIRContactPointMaskingProvider(new DefaultMaskingConfiguration(), new HashSet<String>(), "/telecom", this.factory);
        FHIRContactPoint maskedContactPoint = contactPointMaskingProvider.mask(contactPoint);

        String maskedValue = maskedContactPoint.getValue();
        assertNull(maskedValue);
    }

    @Test
    public void testSystemAbsent() throws Exception {
        String cpJSON = "{\n" +
                "      \"value\": \"0648352638\",\n" +
                "      \"use\": \"mobile\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRContactPoint contactPoint = objectMapper.readValue(cpJSON, FHIRContactPoint.class);

        String value = contactPoint.getValue();

        FHIRContactPointMaskingProvider contactPointMaskingProvider =
                new FHIRContactPointMaskingProvider(new DefaultMaskingConfiguration(), new HashSet<String>(), "/telecom", this.factory);
        FHIRContactPoint maskedContactPoint = contactPointMaskingProvider.mask(contactPoint);

        String maskedValue = maskedContactPoint.getValue();
        assertNotEquals(maskedValue, value);
    }
}

