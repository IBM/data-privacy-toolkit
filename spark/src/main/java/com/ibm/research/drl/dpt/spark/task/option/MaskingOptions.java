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

package com.ibm.research.drl.dpt.spark.task.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class MaskingOptions implements TaskOptions {
    private static final Logger logger = LoggerFactory.getLogger(MaskingOptions.class);

    private final Map<String, DataMaskingTarget> toBeMasked;
    private final Map<String, FieldRelationship> predefinedRelationships;
    private final String maskingProviders;
    private final JsonNode maskingProvidersConfig;


    @JsonCreator
    public MaskingOptions(
            @JsonProperty("toBeMasked") Map<String, DataMaskingTarget> toBeMasked,
            @JsonProperty("predefinedRelationships") Map<String, FieldRelationship> predefinedRelationships,
            @JsonProperty("maskingProviders") String maskingProviders,
            @JsonProperty("maskingProvidersConfig") JsonNode maskingProvidersConfig) {
        this.toBeMasked = toBeMasked;
        this.predefinedRelationships = predefinedRelationships;
        this.maskingProviders = maskingProviders;
        this.maskingProvidersConfig = maskingProvidersConfig;
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

    public JsonNode getMaskingProvidersConfig() {
        return maskingProvidersConfig;
    }
}
