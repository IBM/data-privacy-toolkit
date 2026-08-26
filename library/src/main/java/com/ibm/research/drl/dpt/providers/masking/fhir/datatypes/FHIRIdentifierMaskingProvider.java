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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.Serializable;
import java.util.Set;

/** FHIRIdentifierMaskingProvider FHIR datatype. */
public class FHIRIdentifierMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {

    /** Whether to mask the period element. */
    private final boolean maskPeriod;
    /** Whether to mask the type element. */
    private final boolean maskType;
    /** Whether to mask the system element. */
    private final boolean maskSystem;
    /** Whether to mask the value element. */
    private final boolean maskValue;
    /** Whether to mask the assigner element. */
    private final boolean maskAssigner;
    /** Whether to remove extensions. */
    private final boolean removeExtensions;

    /** Masking provider for the value element. */
    private final MaskingProvider maskingProviderForValue;
    /** Masking provider for the system element. */
    private final MaskingProvider systemMaskingProvider;

    /** Masking provider for the period element. */
    private final FHIRPeriodMaskingProvider periodMaskingProvider;
    /** Masking provider for the type element. */
    private final FHIRCodeableConceptMaskingProvider typeMaskingProvider;
    /** Masking provider for the assigner element. */
    private final FHIRReferenceMaskingProvider assignerMaskingProvider;

    /** JSON path to the value field. */
    private final String VALUE_PATH;
    /** JSON path to the period field. */
    private final String PERIOD_PATH;
    /** JSON path to the type field. */
    private final String TYPE_PATH;
    /** JSON path to the system field. */
    private final String SYSTEM_PATH;
    /** JSON path to the assigner field. */
    private final String ASSIGNER_PATH;

    /**
     * Constructs a FHIRIdentifierMaskingProvider.
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param fieldPath the fieldPath
     * @param factory the factory
     */
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

    @Override
    /**
     * Masks a JsonNode object.
     * @param node the JsonNode to mask
     * @return the masked JsonNode
     */
    public JsonNode mask(JsonNode node) {
        try {
            FHIRIdentifier obj = JsonUtils.MAPPER.treeToValue(node, FHIRIdentifier.class);
            FHIRIdentifier maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    /**
     * Masks a FHIR Identifier object.
     * @param identifier the FHIRIdentifier to mask
     * @return the masked FHIRIdentifier
     */
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

        if (this.maskValue && !isAlreadyMasked(VALUE_PATH)) {
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


