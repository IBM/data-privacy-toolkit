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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAnnotation;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.Set;

public class FHIRAnnotationMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {
    private final String fieldPath;

    private final boolean removeExtensions;
    private final boolean removeText;
    private final boolean removeAuthorString;

    private final boolean maskAuthorReference;
    private final boolean maskTime;
    private final FHIRReferenceMaskingProvider authorReferenceMaskingProvider;
    private final MaskingProvider timeMaskingProvider;

    private final String AUTHORREFERENCE_FIELD_PATH;
    private final String TIME_FIELD_PATH;

    public FHIRAnnotationMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.fieldPath = fieldPath;
        this.TIME_FIELD_PATH = fieldPath + "/time";
        this.AUTHORREFERENCE_FIELD_PATH = fieldPath + "/authorReference";

        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.annotation.removeExtensions");
        this.removeText = maskingConfiguration.getBooleanValue("fhir.annotation.removeText");
        this.removeAuthorString = maskingConfiguration.getBooleanValue("fhir.annotation.removeAuthorString");

        this.maskAuthorReference = maskingConfiguration.getBooleanValue("fhir.annotation.maskAuthorReference");
        this.maskTime = maskingConfiguration.getBooleanValue("fhir.annotation.maskTime");

        this.authorReferenceMaskingProvider = new FHIRReferenceMaskingProvider(
                getConfigurationForSubfield(AUTHORREFERENCE_FIELD_PATH, maskingConfiguration), maskedFields, AUTHORREFERENCE_FIELD_PATH, this.factory);

        this.timeMaskingProvider = this.factory.get(ProviderType.DATETIME, getConfigurationForSubfield(TIME_FIELD_PATH, maskingConfiguration));
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRAnnotation obj = JsonUtils.MAPPER.treeToValue(node, FHIRAnnotation.class);
            FHIRAnnotation maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRAnnotation mask(FHIRAnnotation annotation) {
        if (annotation == null) {
            return null;
        }

        if (this.removeExtensions) {
            annotation.setExtension(null);
        }

        if (this.removeAuthorString) {
            annotation.setAuthorString(null);
        }

        if (this.removeText) {
            annotation.setText(null);
        }

        if (this.maskAuthorReference && !isAlreadyMasked(AUTHORREFERENCE_FIELD_PATH)) {
            FHIRReference reference = annotation.getAuthorReference();
            if (reference != null) {
                annotation.setAuthorReference(this.authorReferenceMaskingProvider.mask(reference));
            }
        }

        if (this.maskTime && !isAlreadyMasked(TIME_FIELD_PATH)) {
            String time = annotation.getTime();
            if (time != null) {
                annotation.setTime(this.timeMaskingProvider.mask(time));
            }
        }

        return annotation;
    }
}


