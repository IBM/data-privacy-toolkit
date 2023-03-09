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
package com.ibm.research.drl.dpt.toolkit.masking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MaskingOptions extends TaskOptions {
    private final Map<String, DataMaskingTarget> toBeMasked;
    private final Map<String, FieldRelationship> predefinedRelationships;
    private final String maskingProviders;
    private final JsonNode maskingProvidersConfig;

    @JsonCreator
    public MaskingOptions(
            @JsonProperty("toBeMasked") Map<String, JsonNode> toBeMasked,
            @JsonProperty("predefinedRelationships") Map<String, FieldRelationship> predefinedRelationships,
            @JsonProperty("maskingProviders") String maskingProviders,
            @JsonProperty("maskingProvidersConfig") JsonNode maskingProvidersConfig
    ) {
        this.toBeMasked = buildToBeMasked(toBeMasked);
        this.predefinedRelationships = predefinedRelationships;
        this.maskingProviders = maskingProviders;
        this.maskingProvidersConfig = maskingProvidersConfig;
    }

    private Map<String, DataMaskingTarget> buildToBeMasked(Map<String, JsonNode> toBeMaskedNodes) {
        return toBeMaskedNodes.entrySet().stream().map(entry ->
                (Map.Entry<String, DataMaskingTarget>) new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        convertValue(entry.getKey(), entry.getValue())
                )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private DataMaskingTarget convertValue(String fieldReference, JsonNode target) {
        if (target.isTextual()) {
            return new DataMaskingTarget(ProviderType.valueOf(target.asText()), fieldReference);
        } else if (target.isObject() && target.has("providerType") && target.has("targetPath")) {
            final JsonNode providerTypeNode = target.get("providerType");

            if (providerTypeNode.isTextual()) {
                final String providerType = providerTypeNode.asText();


                final JsonNode targetPathNode = target.get("targetPath");
                if (targetPathNode.isTextual()) {
                    final String targetPath = targetPathNode.asText();

                    return new DataMaskingTarget(ProviderType.valueOf(providerType), targetPath);
                }
            }
        }
        throw new IllegalArgumentException("Unable to deserialize " + target);
    }

    public JsonNode getMaskingProvidersConfig() {
        return maskingProvidersConfig;
    }

    public Map<String, DataMaskingTarget> getToBeMasked() {
        return toBeMasked;
    }

    public Map<String, FieldRelationship> getPredefinedRelationships() {
        return predefinedRelationships;
    }

    public String getMaskingProviders() {
        return maskingProviders;
    }
}
