package com.ibm.research.drl.dpt.spark.task.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;

import java.util.List;
import java.util.Objects;

public class IdentificationOptions implements TaskOptions {
    private final String localization;
    private final int firstN;
    private final double sampleSize;

    private final IdentificationConfiguration configuration;
    private final JsonNode identifiers;

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty("localization") String localization,
            @JsonProperty(value = "firstN", defaultValue = "0") int firstN,
            @JsonProperty(value = "sampleSize", defaultValue = "0.0") double sampleSize,
            @JsonProperty("identifiers") JsonNode identifiers,
            @JsonProperty("configuration") IdentificationConfiguration configuration
    ) {
        this.localization = localization;
        this.firstN = firstN;
        this.sampleSize = sampleSize;
        this.identifiers = identifiers;
        this.configuration = configuration;
    }

    public String getLocalization() {
        return localization;
    }

    public int getFirstN() {
        return firstN;
    }

    public JsonNode getIdentifiers() {
        return identifiers;
    }

    public double getSampleSize() {
        return sampleSize;
    }

    public IdentificationConfiguration getConfiguration() {
        return Objects.requireNonNullElse(this.configuration, IdentificationConfiguration.DEFAULT);
    }
}
