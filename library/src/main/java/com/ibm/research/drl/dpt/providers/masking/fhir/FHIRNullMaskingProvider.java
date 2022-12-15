/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;

import java.util.Set;

public class FHIRNullMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    public FHIRNullMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fullPath) {
        super("fhir", maskingConfiguration, maskedFields, null);
    }

    public JsonNode mask(JsonNode node) {
        return NullNode.getInstance();
    }
}

