/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.jsonpath.JSONPathExtractor;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRMaskingProviderGenericTest<K> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public void testAlreadyMasked(Set<String> maskedFields,
                                  Set<String> paths,
                                  AbstractComplexMaskingProvider<K> maskingProvider,
                                  String resourceFilename,
                                  Class<K> genericType) throws Exception {

        K original = objectMapper.readValue(this.getClass().getResourceAsStream(resourceFilename), genericType);
        JsonNode originalTree = objectMapper.readTree(this.getClass().getResourceAsStream(resourceFilename));

        K masked = maskingProvider.mask(
                objectMapper.readValue(this.getClass().getResourceAsStream(resourceFilename), genericType));
        JsonNode maskedTree = objectMapper.valueToTree(masked);

        for(String path: paths) {
            assertEquals(JSONPathExtractor.extract(originalTree, path), JSONPathExtractor.extract(maskedTree, path));
        }

    }

}


