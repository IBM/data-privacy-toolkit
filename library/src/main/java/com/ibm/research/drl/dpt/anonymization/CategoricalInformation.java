/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;

public class CategoricalInformation implements ColumnInformation {
    private final ColumnType columnType;
    private final GeneralizationHierarchy hierarchy;
    private final double weight;
    private final int maximumLevel;
    private final boolean forLinking;

    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType) {
        this(hierarchy, columnType, 1.0);
    }

    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, boolean forLinking) {
        this(hierarchy, columnType, 1.0, -1, forLinking);
    }

    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, double weight) {
        this(hierarchy, columnType, weight, -1);
    }

    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, double weight, int maximumLevel) {
        this(hierarchy, columnType, weight, maximumLevel, false);
    }

    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, double weight, int maximumLevel, boolean forLinking) {
        this.columnType = columnType;
        this.hierarchy = hierarchy;
        this.weight = weight;
        this.maximumLevel = maximumLevel;
        this.forLinking = forLinking;
    }

    @JsonCreator
    private CategoricalInformation(
            @JsonProperty("hierarchy") JsonNode hierarchy,
            @JsonProperty("columnType") ColumnType columnType,
            @JsonProperty("weight") double weight,
            @JsonProperty("maximumLevel") int maximumLevel,
            @JsonProperty("forLinking") boolean forLinking) {
        this(
                buildHierarchy(hierarchy),
                columnType,
                weight,
                maximumLevel,
                forLinking
        );
    }

    private static GeneralizationHierarchy buildHierarchy(JsonNode hierarchy) {
        if (hierarchy.isTextual()) {
            return GeneralizationHierarchyFactory.getDefaultHierarchy(hierarchy.asText());
        }
        if (hierarchy.has("terms")) {
            return GeneralizationHierarchyFactory.buildHierarchy(hierarchy);
        }
        return GeneralizationHierarchyFactory.getDefaultHierarchy(hierarchy);
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isForLinking() {
        return forLinking;
    }

    public int getMaximumLevel() {
        return maximumLevel;
    }

    public GeneralizationHierarchy getHierarchy() {
        return this.hierarchy;
    }

    @Override
    @JsonIgnore
    public boolean isCategorical() {
        return true;
    }

    @Override
    public ColumnType getColumnType() {
        return this.columnType;
    }

    @Override
    @JsonIgnore
    public String getRepresentation() {
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("Categorical, type: %s, weight: %f, isForLinking: %s", columnType.name(), weight, forLinking);
    }
}
