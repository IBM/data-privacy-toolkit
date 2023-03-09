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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.Set;

public class FHIRRangeMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean deleteLow;
    private final boolean deleteHigh;
    private final boolean maskLow;
    private final boolean maskHigh;

    private final FHIRQuantityMaskingProvider lowMaskingProvider;
    private final FHIRQuantityMaskingProvider highMaskingProvider;

    private final String LOW_PATH;
    private final String HIGH_PATH;

    public FHIRRangeMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.LOW_PATH = fieldPath + "/low";
        this.HIGH_PATH = fieldPath + "/high";

        this.deleteHigh = maskingConfiguration.getBooleanValue("fhir.range.deleteHigh");
        this.maskHigh = maskingConfiguration.getBooleanValue("fhir.range.maskHigh");
        this.deleteLow = maskingConfiguration.getBooleanValue("fhir.range.deleteLow");
        this.maskLow = maskingConfiguration.getBooleanValue("fhir.range.maskLow");

        this.lowMaskingProvider = new FHIRQuantityMaskingProvider(getConfigurationForSubfield(LOW_PATH, maskingConfiguration),
                maskedFields, LOW_PATH, this.factory);
        this.highMaskingProvider = new FHIRQuantityMaskingProvider(getConfigurationForSubfield(HIGH_PATH, maskingConfiguration),
                maskedFields, HIGH_PATH, this.factory);
    }

    @Override
    public JsonNode mask(JsonNode node) {
        try {
            FHIRRange obj = JsonUtils.MAPPER.treeToValue(node, FHIRRange.class);
            FHIRRange maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRRange mask(FHIRRange range) {
        if (this.deleteHigh) {
            range.setHigh(null);
        } else if (this.maskHigh && !isAlreadyMasked(HIGH_PATH)) {
            range.setHigh(highMaskingProvider.mask(range.getHigh()));
        }


        if (this.deleteLow) {
            range.setLow(null);
        } else if (this.maskLow && !isAlreadyMasked(LOW_PATH)) {
            range.setLow(lowMaskingProvider.mask(range.getLow()));
        }

        return range;
    }
}


