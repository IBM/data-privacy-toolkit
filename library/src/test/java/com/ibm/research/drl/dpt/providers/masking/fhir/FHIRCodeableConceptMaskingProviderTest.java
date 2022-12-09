/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRCodeableConceptMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FHIRCodeableConceptMaskingProviderTest {

    @Test
    public void testBasic() throws  Exception{
        String json = "{\n" +
                "          \"coding\": [\n" +
                "            {\n" +
                "              \"system\": \"http://hl7.org/fhir/care-plan-activity-category\",\n" +
                "              \"code\": \"observation\"\n" +
                "            }\n" +
                "          ], " +
                "          \"text\": \"foo\"\n" +
                "        }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRCodeableConcept codeableConcept = objectMapper.readValue(json, FHIRCodeableConcept.class);

        assertEquals("foo", codeableConcept.getText());

        FHIRCodeableConceptMaskingProvider codingMaskingProvider = new FHIRCodeableConceptMaskingProvider(
                new DefaultMaskingConfiguration(), new HashSet<String>(), "/concept", new MaskingProviderFactory());

        FHIRCodeableConcept maskedCodeableConcept = codingMaskingProvider.mask(codeableConcept);

        assertNotNull(maskedCodeableConcept.getText());
        assertEquals("foo", maskedCodeableConcept.getText());

    }
}


