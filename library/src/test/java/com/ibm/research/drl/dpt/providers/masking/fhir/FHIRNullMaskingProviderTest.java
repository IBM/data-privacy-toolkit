/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2117                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FHIRNullMaskingProviderTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private FHIRNullMaskingProvider maskingProvider;

    @BeforeEach
    public void setUp() {
        maskingProvider = new FHIRNullMaskingProvider(new DefaultMaskingConfiguration(), Collections.EMPTY_SET, null);
    }

    @Test
    public void theMaskingProvidersReplaceANodeWithNull() throws Exception {
        String testJson1 = "{\"test\":\"something\"}";
        JsonNode tree = mapper.readTree(testJson1);

        JsonNode node = maskingProvider.mask(tree.get("test"));

        assertNotNull(node);
        assertSame(node, NullNode.getInstance());
    }
}