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
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FHIRNullMaskingProviderTest {
    private FHIRNullMaskingProvider maskingProvider;

    @BeforeEach
    public void setUp() {
        maskingProvider = new FHIRNullMaskingProvider(new DefaultMaskingConfiguration(), Collections.emptySet(), null);
    }

    @Test
    public void theMaskingProvidersReplaceANodeWithNull() throws Exception {
        String testJson1 = "{\"test\":\"something\"}";
        JsonNode tree = JsonUtils.MAPPER.readTree(testJson1);

        JsonNode node = maskingProvider.mask(tree.get("test"));

        assertNotNull(node);
        assertSame(node, NullNode.getInstance());
    }
}