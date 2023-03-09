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
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.Serializable;
import java.util.Set;

public class FHIRReferenceMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {

    private final boolean maskDisplay;
    private final boolean removeDisplay;
    private final boolean removeExtension;
    private final boolean maskReference;
    private final boolean preserveReferencePrefix;
    private final MaskingProvider referenceMaskingProvider;
    private final MaskingProvider displayMaskingProvider;
    private final Set<String> maskReferenceExcludePrefixList;

    private final String REFERENCE_FIELD_PATH;
    private final String DISPLAY_FIELD_PATH;

    public FHIRReferenceMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, final String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.REFERENCE_FIELD_PATH = fieldPath + "/reference";
        this.DISPLAY_FIELD_PATH = fieldPath + "/display";

        this.maskDisplay = maskingConfiguration.getBooleanValue("fhir.reference.maskDisplay");
        this.removeDisplay = maskingConfiguration.getBooleanValue("fhir.reference.removeDisplay");
        this.removeExtension = maskingConfiguration.getBooleanValue("fhir.reference.removeExtension");
        this.maskReference = maskingConfiguration.getBooleanValue("fhir.reference.maskReference");
        this.preserveReferencePrefix = maskingConfiguration.getBooleanValue("fhir.reference.preserveReferencePrefix");
        this.maskReferenceExcludePrefixList = FHIRMaskingUtils.setFromString(maskingConfiguration.getStringValue("fhir.reference.maskReferenceExcludePrefixList"), true);

        this.referenceMaskingProvider = getMaskingProvider(REFERENCE_FIELD_PATH, maskingConfiguration, this.factory);
        this.displayMaskingProvider = getMaskingProvider(DISPLAY_FIELD_PATH, maskingConfiguration, this.factory);
    }

    @Override
    public JsonNode mask(JsonNode node) {
        try {
            FHIRReference reference = JsonUtils.MAPPER.treeToValue(node, FHIRReference.class);
            FHIRReference maskedReference = mask(reference);
            return JsonUtils.MAPPER.valueToTree(maskedReference);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    private boolean matchPrefix(String value) {
        int slashIndex = value.lastIndexOf('/');
        if (slashIndex == -1) {
            return false;
        }

        String prefix = value.substring(0, slashIndex);

        return this.maskReferenceExcludePrefixList.contains(prefix.toUpperCase());
    }

    public FHIRReference mask(FHIRReference reference) {
        if (reference == null) {
            return null;
        }

        if (this.removeDisplay) {
            reference.setDisplay(null);
        } else if (this.maskDisplay && !isAlreadyMasked(DISPLAY_FIELD_PATH)) {
            String display = reference.getDisplay();
            if (display != null) {
                reference.setDisplay(displayMaskingProvider.mask(display));
            }
        }

        if (this.removeExtension) {
            reference.setExtension(null);
        }

        if (this.maskReference && !isAlreadyMasked(REFERENCE_FIELD_PATH)) {
            String refValue = reference.getReference();
            if (refValue != null) {
                boolean maskingRequired = this.maskReferenceExcludePrefixList.isEmpty() || !matchPrefix(refValue);

                if (maskingRequired) {
                    String maskedReference = FHIRMaskingUtils.maskResourceId(refValue, this.preserveReferencePrefix, referenceMaskingProvider);
                    reference.setReference(maskedReference);
                }
            }
        }

        return reference;
    }
}


