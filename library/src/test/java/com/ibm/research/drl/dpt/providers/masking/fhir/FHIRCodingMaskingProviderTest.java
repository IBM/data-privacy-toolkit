/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRCodingMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FHIRCodingMaskingProviderTest {

    @Test
    public void testBasic() throws Exception {
        String json = "{\n" +
                "              \"system\": \"http://hl7.org/fhir/care-plan-activity-category\",\n" +
                "              \"version\" : \"1.2\"," +
                "              \"code\": \"observation\"\n" +
                "            }";

        ObjectMapper objectMapper = new ObjectMapper();

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            FHIRCoding coding = objectMapper.readValue(json, FHIRCoding.class);
            assertEquals("1.2", coding.getVersion());
            FHIRCodingMaskingProvider codingMaskingProvider = new FHIRCodingMaskingProvider(new DefaultMaskingConfiguration(),
                    new HashSet<String>(), "/foo", new MaskingProviderFactory());
            FHIRCoding maskedCoding = codingMaskingProvider.mask(coding);

            if(!maskedCoding.getVersion().equals("1.2")) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }
}


