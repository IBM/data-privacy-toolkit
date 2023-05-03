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
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.util.JsonUtils;
import com.ibm.research.drl.jsonpath.JSONPathExtractor;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRMaskingProviderGenericTest<K> {

    public void testAlreadyMasked(Set<String> maskedFields,
                                  Set<String> paths,
                                  AbstractComplexMaskingProvider<K> maskingProvider,
                                  String resourceFilename,
                                  Class<K> genericType) throws Exception {

        try (
                InputStream originalIS = FHIRMaskingProviderGenericTest.class.getResourceAsStream(resourceFilename);
                InputStream maskedIS = FHIRMaskingProviderGenericTest.class.getResourceAsStream(resourceFilename)) {
            K original = JsonUtils.MAPPER.readValue(originalIS, genericType);
            JsonNode originalTree = JsonUtils.MAPPER.readTree(this.getClass().getResourceAsStream(resourceFilename));

            K masked = maskingProvider.mask(
                    JsonUtils.MAPPER.readValue(maskedIS, genericType));
            JsonNode maskedTree = JsonUtils.MAPPER.valueToTree(masked);

            for (String path : paths) {
                assertEquals(JSONPathExtractor.extract(originalTree, path), JSONPathExtractor.extract(maskedTree, path));
            }
        }
    }
}


