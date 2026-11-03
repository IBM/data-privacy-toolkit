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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Column information for numerical quasi-identifier columns, holding the sorted value list
 * and precomputed range metadata.
 */
public class NumericalRange implements ColumnInformation {
    /** The column type. */
    private final ColumnType columnType;
    /** The range (max − min) of values, or {@code null} if empty. */
    private final Double range;
    /** The minimum value, or {@code null} if empty. */
    private final Double low;
    /** The maximum value, or {@code null} if empty. */
    private final Double high;
    /** String representation of the interval. */
    private final String representation;
    /** Number of distinct values. */
    private final int numberOfValues;
    /** Sorted list of values. */
    private final List<Double> sortedValues;
    /** Map from value to its zero-based position in the sorted list. */
    private final Map<Double, Integer> positionMap;
    /** The column weight. */
    private final double weight;
    /** Whether this range is used for linking. */
    private final boolean isForLinking;

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isForLinking() {
        return isForLinking;
    }

    /**
     * Returns the number of distinct values in this range.
     *
     * @return the number of values
     */
    @JsonIgnore
    public int getNumberOfValues() {
        return numberOfValues;
    }

    @Override
    @JsonIgnore
    public boolean isCategorical() {
        return false;
    }

    @Override
    public ColumnType getColumnType() {
        return this.columnType;
    }

    @Override
    @JsonIgnore
    public String getRepresentation() {
        return representation;
    }

    /**
     * Returns the range (max - min) of values in this column, or {@code null} if empty.
     *
     * @return the range
     */
    @JsonIgnore
    public Double getRange() {
        return this.range;
    }

    /**
     * Returns the position (rank) of the given value in the sorted values list.
     *
     * @param value the value to look up
     * @return the zero-based position
     */
    @JsonIgnore
    public int getPosition(Double value) {
        return this.positionMap.get(value);
    }

    /**
     * Returns the minimum value in this range, or {@code null} if empty.
     *
     * @return the lower bound
     */
    @JsonIgnore
    public Double getLow() {
        return low;
    }

    /**
     * Returns the maximum value in this range, or {@code null} if empty.
     *
     * @return the upper bound
     */
    @JsonIgnore
    public Double getHigh() {
        return high;
    }

    /**
     * Constructs a NumericalRange with default weight and no linking.
     *
     * @param sortedValues the sorted list of values
     * @param columnType   the column type
     */
    public NumericalRange(List<Double> sortedValues, ColumnType columnType) {
        this(sortedValues, columnType, 1.0, false);
    }

    /**
     * Constructs a NumericalRange from its constituent properties.
     *
     * @param sortedValues sorted list of numeric values defining the range
     * @param columnType   the column type
     * @param weight       the weight assigned to this range
     * @param forLinking   whether this range is used for record linking
     */
    @JsonCreator
    public NumericalRange(
            @JsonProperty("sortedValues") List<Double> sortedValues,
            @JsonProperty("columnType") ColumnType columnType,
            @JsonProperty("weight") double weight,
            @JsonProperty("forLinking") boolean forLinking) {
        this.columnType = columnType;
        this.numberOfValues = sortedValues.size();
        if (sortedValues.size() > 0) {
            this.low = sortedValues.get(0);
            this.high = sortedValues.get(sortedValues.size() - 1);
            this.range = this.high - this.low;
        } else {
            this.low = null;
            this.high = null;
            this.range = null;
        }

        this.isForLinking = forLinking;
        this.representation = String.format("[%f-%f]", this.low, this.high);
        this.sortedValues = sortedValues;
        this.weight = weight;

        this.positionMap = new HashMap<>();
        for (int i = 0; i < sortedValues.size(); i++) {
            Double elem = sortedValues.get(i);
            this.positionMap.put(elem, i);
        }
    }

    /**
     * Returns the sorted list of values.
     *
     * @return the sorted values
     */
    public List<Double> getSortedValues() {
        return sortedValues;
    }
}
