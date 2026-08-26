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
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;

/**
 * Column information for categorical quasi-identifier columns, backed by a generalization hierarchy.
 */
public class CategoricalInformation implements ColumnInformation {
    private final ColumnType columnType;
    private final GeneralizationHierarchy hierarchy;
    private final double weight;
    private final int maximumLevel;
    private final boolean forLinking;

    /**
     * Constructs a CategoricalInformation with default weight and no linking.
     *
     * @param hierarchy  the generalization hierarchy
     * @param columnType the column type
     */
    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType) {
        this(hierarchy, columnType, 1.0);
    }

    /**
     * Constructs a CategoricalInformation with default weight and specified linking flag.
     *
     * @param hierarchy  the generalization hierarchy
     * @param columnType the column type
     * @param forLinking whether this column is used for linking
     */
    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, boolean forLinking) {
        this(hierarchy, columnType, 1.0, -1, forLinking);
    }

    /**
     * Constructs a CategoricalInformation with a specified weight and no maximum level.
     *
     * @param hierarchy  the generalization hierarchy
     * @param columnType the column type
     * @param weight     the column weight
     */
    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, double weight) {
        this(hierarchy, columnType, weight, -1);
    }

    /**
     * Constructs a CategoricalInformation with a specified weight and maximum level.
     *
     * @param hierarchy    the generalization hierarchy
     * @param columnType   the column type
     * @param weight       the column weight
     * @param maximumLevel the maximum generalization level, or -1 for no limit
     */
    public CategoricalInformation(GeneralizationHierarchy hierarchy, ColumnType columnType, double weight, int maximumLevel) {
        this(hierarchy, columnType, weight, maximumLevel, false);
    }

    /**
     * Constructs a CategoricalInformation with all parameters specified.
     *
     * @param hierarchy    the generalization hierarchy
     * @param columnType   the column type
     * @param weight       the column weight
     * @param maximumLevel the maximum generalization level, or -1 for no limit
     * @param forLinking   whether this column is used for linking
     */
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

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isForLinking() {
        return forLinking;
    }

    /**
     * Returns the maximum generalization level allowed for this column.
     *
     * @return the maximum level, or -1 if no limit
     */
    public int getMaximumLevel() {
        return maximumLevel;
    }

    /**
     * Returns the generalization hierarchy for this column.
     *
     * @return the generalization hierarchy
     */
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
