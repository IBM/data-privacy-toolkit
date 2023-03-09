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

public class NumericalRange implements ColumnInformation {
    private final ColumnType columnType;
    private final Double range;
    private final Double low;
    private final Double high;
    private final String representation;
    private final int numberOfValues;
    private final List<Double> sortedValues;
    private final Map<Double, Integer> positionMap;
    private final double weight;
    private final boolean isForLinking;

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isForLinking() {
        return isForLinking;
    }

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

    @JsonIgnore
    public Double getRange() {
        return this.range;
    }

    @JsonIgnore
    public int getPosition(Double value) {
        return this.positionMap.get(value);
    }

    @JsonIgnore
    public Double getLow() {
        return low;
    }

    @JsonIgnore
    public Double getHigh() {
        return high;
    }

    public NumericalRange(List<Double> sortedValues, ColumnType columnType) {
        this(sortedValues, columnType, 1.0, false);
    }

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

    public List<Double> getSortedValues() {
        return sortedValues;
    }
}
