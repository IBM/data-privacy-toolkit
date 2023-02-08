package com.ibm.research.drl.dpt.spark.task.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class IdentificationOptions implements TaskOptions {
    private final String localization;
    private final int firstN;
    private final double sampleSize;
    private final List<JsonNode> identifiers;

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty("localization") String localization,
            @JsonProperty(value = "firstN", defaultValue = "0") int firstN,
            @JsonProperty(value = "sampleSize", defaultValue = "0.0") double sampleSize,
            @JsonProperty("identifiers") List<JsonNode> identifiers,
            @JsonProperty("onlyFields") List<String> onlyField
    ) {
        this.localization = localization;
        this.firstN = firstN;
        this.sampleSize = sampleSize;
        this.identifiers = identifiers;
    }

    public String getLocalization() {
        return localization;
    }

    public int getFirstN() {
        return firstN;
    }

    public List<JsonNode> getIdentifiers() {
        return identifiers;
    }

    public double getSampleSize() {
        return sampleSize;
    }
}
