/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.util.Set;

public class FHIRQuantityMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final String VALUE_PATH;
    private final String SYSTEM_PATH;
    private final String CODE_PATH;

    private final boolean maskValue;
    private final boolean maskSystem;
    private final boolean maskCode;

    private final MaskingProvider valueMaskingProvider;
    private final MaskingProvider systemMaskingProvider;
    private final MaskingProvider codeMaskingProvider;

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

    public JsonNode mask(JsonNode node) {
        try {
            FHIRQuantity obj = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRQuantity.class);
            FHIRQuantity maskedObj= mask(obj);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRQuantity mask(FHIRQuantity quantity) {
        if (quantity == null) {
            return null;
        }

        if (this.maskValue && !isAlreadyMasked(VALUE_PATH)) {
            float value = quantity.getValue();
            quantity.setValue(Float.valueOf(valueMaskingProvider.mask(Float.toString(value))));
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


