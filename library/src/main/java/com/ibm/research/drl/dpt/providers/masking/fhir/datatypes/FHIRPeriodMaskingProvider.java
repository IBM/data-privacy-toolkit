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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.Serializable;
import java.util.Set;

public class FHIRPeriodMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {

    private final boolean maskStart;
    private final boolean maskEnd;
    private final boolean removeStart;
    private final boolean removeEnd;

    private final MaskingProvider startMaskingProvider;
    private final MaskingProvider endMaskingProvider;

    private final String START_PATH;
    private final String END_PATH;

    public FHIRPeriodMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.START_PATH = fieldPath + "/start";
        this.END_PATH = fieldPath + "/end";

        this.maskStart = maskingConfiguration.getBooleanValue("fhir.period.maskStart");
        this.maskEnd = maskingConfiguration.getBooleanValue("fhir.period.maskEnd");
        this.removeStart = maskingConfiguration.getBooleanValue("fhir.period.removeStart");
        this.removeEnd = maskingConfiguration.getBooleanValue("fhir.period.removeEnd");

        MaskingConfiguration startMaskingConfiguration = getConfigurationForSubfield(START_PATH, maskingConfiguration);
        String startDefaultMaskingProvider = startMaskingConfiguration.getStringValue("default.masking.provider");
        this.startMaskingProvider = this.factory.get(ProviderType.valueOf(startDefaultMaskingProvider), startMaskingConfiguration);

        MaskingConfiguration endMaskingConfiguration = getConfigurationForSubfield(END_PATH, maskingConfiguration);
        String endDefaultMaskingProvider = endMaskingConfiguration.getStringValue("default.masking.provider");
        this.endMaskingProvider = this.factory.get(ProviderType.valueOf(endDefaultMaskingProvider), endMaskingConfiguration);
    }

    @Override
    public JsonNode mask(JsonNode node) {
        try {
            FHIRPeriod obj = JsonUtils.MAPPER.treeToValue(node, FHIRPeriod.class);
            FHIRPeriod maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRPeriod mask(FHIRPeriod period) {
        if (period == null) {
            return null;
        }

        if (this.removeEnd) {
            period.setEnd(null);
        } else if (this.maskEnd && !isAlreadyMasked(END_PATH)) {
            String end = period.getEnd();
            if (end != null) {
                period.setEnd(endMaskingProvider.mask(end));
            }

        }

        if (this.removeStart) {
            period.setStart(null);
        } else if (this.maskStart && !isAlreadyMasked(START_PATH)) {
            String start = period.getStart();
            if (start != null) {
                period.setStart(startMaskingProvider.mask(start));
            }
        }

        return period;
    }
}


