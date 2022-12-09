/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAnnotation;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRAnnotationMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FHIRAnnotationMaskingProviderTest {

    @Test
    public void testBasic() throws Exception {
        String json = "{" +
                "\"text\" : \"foobar\"" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();

        FHIRAnnotation annotation = objectMapper.readValue(json, FHIRAnnotation.class);
        assertNotNull(annotation.getText());

        FHIRAnnotationMaskingProvider fhirAnnotationMaskingProvider = new FHIRAnnotationMaskingProvider(
                new DefaultMaskingConfiguration(), new HashSet<String>(), "/", new MaskingProviderFactory());

        FHIRAnnotation maskedAnnotation = fhirAnnotationMaskingProvider.mask(annotation);
        assertNull(maskedAnnotation.getText());
    }
}


