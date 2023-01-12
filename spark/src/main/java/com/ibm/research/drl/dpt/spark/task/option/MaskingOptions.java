package com.ibm.research.drl.dpt.spark.task.option;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.util.Map;

public class MaskingOptions {
    private final Map<String, DataMaskingTarget> toBeMasked;
    private final Map<String, FieldRelationship> predefinedRelationships;
    private final String maskingProviders;
    private final JsonNode maskingProvidersConfig;


    public MaskingOptions(Map<String, DataMaskingTarget> toBeMasked, Map<String, FieldRelationship> predefinedRelationships, String maskingProviders, JsonNode maskingProvidersConfig) {
        this.toBeMasked = toBeMasked;
        this.predefinedRelationships = predefinedRelationships;
        this.maskingProviders = maskingProviders;
        this.maskingProvidersConfig = maskingProvidersConfig;
    }
}
