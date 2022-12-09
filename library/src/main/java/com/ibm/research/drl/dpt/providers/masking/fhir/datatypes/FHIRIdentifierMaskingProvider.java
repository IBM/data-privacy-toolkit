/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.io.Serializable;
import java.util.Set;

public class FHIRIdentifierMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {

    private final boolean maskPeriod;
    private final boolean maskType;
    private final boolean maskSystem;
    private final boolean maskValue;
    private final boolean maskAssigner;
    private final boolean removeExtensions;

    private final MaskingProvider maskingProviderForValue;
    private final MaskingProvider systemMaskingProvider;


    private final FHIRPeriodMaskingProvider periodMaskingProvider;
    private final FHIRCodeableConceptMaskingProvider typeMaskingProvider;
    private final FHIRReferenceMaskingProvider assignerMaskingProvider;

    private final String VALUE_PATH;
    private final String PERIOD_PATH;
    private final String TYPE_PATH;
    private final String SYSTEM_PATH;
    private final String ASSIGNER_PATH;

    public FHIRIdentifierMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, final String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.VALUE_PATH = fieldPath + "/value";
        this.PERIOD_PATH = fieldPath + "/period";
        this.TYPE_PATH = fieldPath + "/type";
        this.SYSTEM_PATH = fieldPath + "/system";
        this.ASSIGNER_PATH = fieldPath + "/assigner";

        this.maskPeriod = maskingConfiguration.getBooleanValue("fhir.identifier.maskPeriod");
        this.maskType = maskingConfiguration.getBooleanValue("fhir.identifier.maskType");
        this.maskSystem = maskingConfiguration.getBooleanValue("fhir.identifier.maskSystem");
        this.maskValue = maskingConfiguration.getBooleanValue("fhir.identifier.maskValue");
        this.maskAssigner = maskingConfiguration.getBooleanValue("fhir.identifier.maskAssigner");

        this.periodMaskingProvider = new FHIRPeriodMaskingProvider(getConfigurationForSubfield(PERIOD_PATH, maskingConfiguration),
                maskedFields, PERIOD_PATH, this.factory);
        this.typeMaskingProvider = new FHIRCodeableConceptMaskingProvider(getConfigurationForSubfield(TYPE_PATH, maskingConfiguration),
                maskedFields, TYPE_PATH, this.factory);
        this.assignerMaskingProvider = new FHIRReferenceMaskingProvider(getConfigurationForSubfield(ASSIGNER_PATH, maskingConfiguration),
                maskedFields, ASSIGNER_PATH, this.factory);

        this.maskingProviderForValue = getMaskingProvider(VALUE_PATH, maskingConfiguration, this.factory);
        this.systemMaskingProvider = getMaskingProvider(SYSTEM_PATH, maskingConfiguration, this.factory);

        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.identifier.removeExtensions");
    }

    private String maskValue(String value) {
        return maskingProviderForValue.mask(value);
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRIdentifier obj = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRIdentifier.class);
            FHIRIdentifier maskedObj= mask(obj);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRIdentifier mask(FHIRIdentifier identifier) {
        if (identifier == null) {
            return null;
        }

        if (this.maskPeriod && !isAlreadyMasked(PERIOD_PATH)) {
            identifier.setPeriod(periodMaskingProvider.mask(identifier.getPeriod()));
        }

        if (this.maskType && !isAlreadyMasked(TYPE_PATH)) {
            identifier.setType(typeMaskingProvider.mask(identifier.getType()));
        }

        if (this.maskSystem && !isAlreadyMasked(SYSTEM_PATH)) {
            String system = identifier.getSystem();
            if (system != null) {
                identifier.setSystem(systemMaskingProvider.mask(system));
            }
        }

        if (this.maskAssigner && !isAlreadyMasked(ASSIGNER_PATH)) {
            identifier.setAssigner(assignerMaskingProvider.mask(identifier.getAssigner()));
        }

        if(this.maskValue && !isAlreadyMasked(VALUE_PATH)) {
            String value = identifier.getValue();
            if (value != null) {
                identifier.setValue(maskValue(value));
            }
        }

        if (this.removeExtensions) {
            identifier.setExtension(null);
        }

        return identifier;
    }
}


