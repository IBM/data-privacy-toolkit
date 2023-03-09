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


