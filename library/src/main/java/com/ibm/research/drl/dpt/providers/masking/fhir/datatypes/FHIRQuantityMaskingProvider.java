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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.Set;

/** FHIRQuantityMaskingProvider FHIR datatype. */
public class FHIRQuantityMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    /** JSON path to the value field. */
    private final String VALUE_PATH;
    /** JSON path to the system field. */
    private final String SYSTEM_PATH;
    /** JSON path to the code field. */
    private final String CODE_PATH;

    /** Whether to mask the value element. */
    private final boolean maskValue;
    /** Whether to mask the system element. */
    private final boolean maskSystem;
    /** Whether to mask the code element. */
    private final boolean maskCode;

    /** Masking provider for the value element. */
    private final MaskingProvider valueMaskingProvider;
    /** Masking provider for the system element. */
    private final MaskingProvider systemMaskingProvider;
    /** Masking provider for the code element. */
    private final MaskingProvider codeMaskingProvider;

    /**
     * Constructs a FHIRQuantityMaskingProvider.
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param fieldPath the fieldPath
     * @param factory the factory
     */
    public FHIRQuantityMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.VALUE_PATH = fieldPath + "/value";
        this.CODE_PATH = fieldPath + "/code";
        this.SYSTEM_PATH = fieldPath + "/system";

        this.maskValue = maskingConfiguration.getBooleanValue("fhir.quantity.maskValue");
        this.maskSystem = maskingConfiguration.getBooleanValue("fhir.quantity.maskSystem");
        this.maskCode = maskingConfiguration.getBooleanValue("fhir.quantity.maskCode");

        this.valueMaskingProvider = getMaskingProvider(VALUE_PATH, maskingConfiguration, this.factory);
        this.systemMaskingProvider = getMaskingProvider(SYSTEM_PATH, maskingConfiguration, this.factory);
        this.codeMaskingProvider = getMaskingProvider(CODE_PATH, maskingConfiguration, this.factory);

    }

    @Override
    /**
     * Masks a JsonNode object.
     * @param node the JsonNode to mask
     * @return the masked JsonNode
     */
    public JsonNode mask(JsonNode node) {
        try {
            FHIRQuantity obj = JsonUtils.MAPPER.treeToValue(node, FHIRQuantity.class);
            FHIRQuantity maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    /**
     * Masks a FHIR Quantity object.
     * @param quantity the FHIRQuantity to mask
     * @return the masked FHIRQuantity
     */
    public FHIRQuantity mask(FHIRQuantity quantity) {
        if (quantity == null) {
            return null;
        }

        if (this.maskValue && !isAlreadyMasked(VALUE_PATH)) {
            float value = quantity.getValue();
            quantity.setValue(Float.parseFloat(valueMaskingProvider.mask(Float.toString(value))));
        }

        if (this.maskSystem && !isAlreadyMasked(SYSTEM_PATH)) {
            String system = quantity.getSystem();
            if (system != null) {
                quantity.setSystem(systemMaskingProvider.mask(system));
            }
        }

        if (this.maskCode && !isAlreadyMasked(CODE_PATH)) {
            String code = quantity.getCode();
            if (code != null) {
                quantity.setCode(codeMaskingProvider.mask(code));
            }
        }

        return quantity;
    }
}


