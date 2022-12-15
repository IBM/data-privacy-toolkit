/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRNarrativeMaskingProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FHIRNarrativeMaskingProviderTest {

    @Test
    public void testBasic() throws Exception {
        String json = "{\n" +
                "              \"status\" : \"1.2\"," +
                "              \"div\": \"divcontents\"\n" +
                "            }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRNarrative narrative = objectMapper.readValue(json, FHIRNarrative.class);

        assertEquals("divcontents", narrative.getDiv());

        FHIRNarrativeMaskingProvider narrativeMaskingProvider = new FHIRNarrativeMaskingProvider(new DefaultMaskingConfiguration());
        FHIRNarrative maskedNarrative = narrativeMaskingProvider.mask(narrative);

        assertNull(maskedNarrative.getDiv());
    }
}


