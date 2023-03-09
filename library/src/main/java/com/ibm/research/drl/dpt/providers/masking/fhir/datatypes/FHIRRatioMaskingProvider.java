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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRatio;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.Set;

public class FHIRRatioMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {
    private final boolean deleteNumerator;
    private final boolean deleteDenominator;
    private final boolean maskNumerator;
    private final boolean maskDenominator;

    private final FHIRQuantityMaskingProvider numeratorMaskingProvider;
    private final FHIRQuantityMaskingProvider denominatorMaskingProvider;

    private final String NUMERATOR_PATH;
    private final String DENOMINATOR_PATH;

    public FHIRRatioMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.NUMERATOR_PATH = fieldPath + "/numerator";
        this.DENOMINATOR_PATH = fieldPath + "/denominator";

        this.deleteDenominator = maskingConfiguration.getBooleanValue("fhir.ratio.deleteDenominator");
        this.maskDenominator = maskingConfiguration.getBooleanValue("fhir.ratio.maskDenominator");
        this.deleteNumerator = maskingConfiguration.getBooleanValue("fhir.ratio.deleteNumerator");
        this.maskNumerator = maskingConfiguration.getBooleanValue("fhir.ratio.maskNumerator");

        this.numeratorMaskingProvider = new FHIRQuantityMaskingProvider(getConfigurationForSubfield(NUMERATOR_PATH, maskingConfiguration), maskedFields, NUMERATOR_PATH, this.factory);
        this.denominatorMaskingProvider = new FHIRQuantityMaskingProvider(getConfigurationForSubfield(DENOMINATOR_PATH, maskingConfiguration), maskedFields, DENOMINATOR_PATH, this.factory);
    }

    @Override
    public JsonNode mask(JsonNode node) {
        try {
            FHIRRatio obj = JsonUtils.MAPPER.treeToValue(node, FHIRRatio.class);
            FHIRRatio maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRRatio mask(FHIRRatio ratio) {
        if (this.deleteDenominator) {
            ratio.setDenominator(null);
        } else if (this.maskDenominator && !isAlreadyMasked(DENOMINATOR_PATH)) {
            ratio.setDenominator(denominatorMaskingProvider.mask(ratio.getDenominator()));
        }


        if (this.deleteNumerator) {
            ratio.setNumerator(null);
        } else if (this.maskNumerator && !isAlreadyMasked(NUMERATOR_PATH)) {
            ratio.setNumerator(numeratorMaskingProvider.mask(ratio.getNumerator()));
        }

        return ratio;
    }
}


