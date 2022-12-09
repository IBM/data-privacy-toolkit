/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.io.Serializable;
import java.util.Set;

public class FHIRReferenceMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable{

    private final boolean maskDisplay;
    private final boolean removeDisplay;
    private final boolean removeExtension;
    private final boolean maskReference;
    private final boolean preserveReferencePrefix;
    private final MaskingProvider referenceMaskingProvider;
    private final MaskingProvider displayMaskingProvider;
    private final Set<String> maskReferenceExcludePrefixList;

    private final static ObjectMapper objectMapper = new ObjectMapper();

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

    public JsonNode mask(JsonNode node) {
        try {
            FHIRReference reference = objectMapper.treeToValue(node, FHIRReference.class);
            FHIRReference maskedReference = mask(reference);
            return objectMapper.valueToTree(maskedReference);
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
        }
        else if (this.maskDisplay && !isAlreadyMasked(DISPLAY_FIELD_PATH)) {
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
                boolean maskingRequired = true;

                if (!this.maskReferenceExcludePrefixList.isEmpty() && matchPrefix(refValue)) {
                    maskingRequired = false;
                }

                if (maskingRequired) {
                    String maskedReference = FHIRMaskingUtils.maskResourceId(refValue, this.preserveReferencePrefix, referenceMaskingProvider);
                    reference.setReference(maskedReference);
                }
            }
        }

        return reference;
    }
}


