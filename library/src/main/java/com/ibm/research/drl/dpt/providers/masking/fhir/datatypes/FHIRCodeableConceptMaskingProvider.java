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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class FHIRCodeableConceptMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean maskText;
    private final boolean maskCoding;

    private final FHIRCodingMaskingProvider codingMaskingProvider;

    private final MaskingProvider textMaskingProvider;

    private final String CODING_FIELD_PATH;
    private final String TEXT_PATH;

    public FHIRCodeableConceptMaskingProvider(MaskingConfiguration maskingConfiguration,
                                              Set<String> fieldNames, final String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, fieldNames, factory);

        this.CODING_FIELD_PATH = fieldPath + "/coding";
        this.TEXT_PATH = fieldPath + "/text";

        this.maskText = maskingConfiguration.getBooleanValue("fhir.codeableconcept.maskText");
        this.maskCoding = maskingConfiguration.getBooleanValue("fhir.codeableconcept.maskCoding");

        this.codingMaskingProvider = new FHIRCodingMaskingProvider(getConfigurationForSubfield(CODING_FIELD_PATH, maskingConfiguration),
                fieldNames, fieldPath, this.factory);

        this.textMaskingProvider = getMaskingProvider(TEXT_PATH, maskingConfiguration, this.factory);
    }

    public void maskCodingElements(FHIRCodeableConcept concept) {
        Collection<FHIRCoding> codings = concept.getCoding();
        if (codings == null || codings.isEmpty()) {
            return;
        }

        Collection<FHIRCoding> maskedCodings = new ArrayList<>();
        for (FHIRCoding coding : codings) {
            maskedCodings.add(this.codingMaskingProvider.mask(coding));
        }

        concept.setCoding(maskedCodings);
    }

    @Override
    public JsonNode mask(JsonNode node) {
        try {
            FHIRCodeableConcept cc = JsonUtils.MAPPER.treeToValue(node, FHIRCodeableConcept.class);
            FHIRCodeableConcept maskedCc = mask(cc);
            return JsonUtils.MAPPER.valueToTree(maskedCc);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRCodeableConcept mask(FHIRCodeableConcept concept) {
        if (concept == null) {
            return null;
        }

        if (this.maskText && !isAlreadyMasked(TEXT_PATH)) {
            String text = concept.getText();
            if (text != null) {
                concept.setText(textMaskingProvider.mask(text));
            }
        }

        if (this.maskCoding && !isAlreadyMasked(CODING_FIELD_PATH)) {
            maskCodingElements(concept);
        }

        return concept;
    }
}


