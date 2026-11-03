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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.security.SecureRandom;
import java.util.Set;

/** FHIRContactPointMaskingProvider FHIR datatype. */
public class FHIRContactPointMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {
    /** Whether to randomise the use element. */
    private final boolean randomizeUse;
    /** Whether to remove extensions. */
    private final boolean removeExtensions;
    /** Whether to mask the value element. */
    private final boolean maskValue;
    /** Whether to mask the period element. */
    private final boolean maskPeriod;
    /** Whether to remove the system element. */
    private final boolean removeSystem;

    /** Generic random masking provider. */
    private final MaskingProvider randomMaskingProvider;
    /** Masking provider for the period element. */
    private final FHIRPeriodMaskingProvider periodMaskingProvider;
    /** Masking provider for phone values. */
    private final MaskingProvider phoneMaskingProvider;
    /** Masking provider for email values. */
    private final MaskingProvider emailMaskingProvider;
    /** Masking provider for URL values. */
    private final MaskingProvider urlMaskingProvider;

    /** JSON path to the value field. */
    private final String valuePath;
    /** JSON path to the use field. */
    private final String usePath;
    /** JSON path to the period field. */
    private final String periodPath;

    /** Shared secure random source. */
    private final static SecureRandom random = new SecureRandom();
    /** Valid contact point use values per the FHIR specification. */
    private final static String[] useValues = new String[]{"home", "work", "temp", "old", "mobile"};

    /**
     * Constructs a FHIRContactPointMaskingProvider.
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param fieldPath the fieldPath
     * @param factory the factory
     */
    public FHIRContactPointMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.valuePath = fieldPath + "/value";
        this.usePath = fieldPath + "/use";
        this.periodPath = fieldPath + "/period";

        this.randomMaskingProvider = getMaskingProvider(valuePath, maskingConfiguration, this.factory);
        this.phoneMaskingProvider = this.factory.get(ProviderType.PHONE, getConfigurationForSubfield(valuePath, maskingConfiguration));
        this.emailMaskingProvider = this.factory.get(ProviderType.EMAIL, getConfigurationForSubfield(valuePath, maskingConfiguration));
        this.urlMaskingProvider = this.factory.get(ProviderType.URL, getConfigurationForSubfield(valuePath, maskingConfiguration));

        this.randomizeUse = maskingConfiguration.getBooleanValue("fhir.contactPoint.randomizeUse");
        this.maskValue = maskingConfiguration.getBooleanValue("fhir.contactPoint.maskValue");
        this.maskPeriod = maskingConfiguration.getBooleanValue("fhir.contactPoint.maskPeriod");
        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.contactPoint.removeExtensions");
        this.removeSystem = maskingConfiguration.getBooleanValue("fhir.contactPoint.removeSystem");

        this.periodMaskingProvider = new FHIRPeriodMaskingProvider(getConfigurationForSubfield(periodPath, maskingConfiguration), maskedFields, periodPath, this.factory);
    }

    /**
     * Masks a JsonNode object.
     * @param node the JsonNode to mask
     * @return the masked JsonNode
     */
    public JsonNode mask(JsonNode node) {
        try {
            FHIRContactPoint cc = JsonUtils.MAPPER.treeToValue(node, FHIRContactPoint.class);
            FHIRContactPoint maskedCc = mask(cc);
            return JsonUtils.MAPPER.valueToTree(maskedCc);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    /**
     * Masks a FHIR ContactPoint object.
     * @param contactPoint the FHIRContactPoint to mask
     * @return the masked FHIRContactPoint
     */
    public FHIRContactPoint mask(FHIRContactPoint contactPoint) {
        if (contactPoint == null) {
            return null;
        }

        if (this.removeSystem) {
            contactPoint.setSystem(null);
        }

        String originalValue = contactPoint.getValue();

        if (this.maskValue && !isAlreadyMasked(valuePath) && originalValue != null) {
            String maskedValue;
            String system = contactPoint.getSystem();

            if (system == null) {
                maskedValue = randomMaskingProvider.mask(originalValue);
            } else {
                /* https://www.hl7.org/fhir/valueset-contact-point-system.html */
                switch (system) {
                    case "email":
                        maskedValue = emailMaskingProvider.mask(originalValue);
                        break;
                    case "other":
                        maskedValue = urlMaskingProvider.mask(originalValue);
                        break;
                    default:
                        maskedValue = phoneMaskingProvider.mask(originalValue);
                        break;
                }
            }

            contactPoint.setValue(maskedValue);
        }

        if (this.randomizeUse && !isAlreadyMasked(usePath)) {
            String randomUse = useValues[random.nextInt(useValues.length)];
            contactPoint.setUse(randomUse);
        }

        if (this.maskPeriod && !isAlreadyMasked(periodPath)) {
            contactPoint.setPeriod(periodMaskingProvider.mask(contactPoint.getPeriod()));
        }

        if (this.removeExtensions) {
            contactPoint.setExtension(null);
        }

        return contactPoint;
    }
}


