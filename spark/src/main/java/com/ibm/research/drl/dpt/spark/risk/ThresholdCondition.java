/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThresholdCondition {
    private final AggregationType aggregationType;
    private final Condition condition;
    private final Object value;
    private final String columnName;
    private final boolean trueOnNull;

    @JsonCreator
    public ThresholdCondition(
            @JsonProperty("aggregationType") final AggregationType aggregationType,
            @JsonProperty("condition") final Condition condition,
            @JsonProperty("value") final Object value,
            @JsonProperty("columnName") final String columnName,
            @JsonProperty(value = "trueOnNull", defaultValue = "false") final boolean trueOnNull) {
        this.aggregationType = aggregationType;
        this.condition = condition;
        this.value = value;
        this.columnName = columnName;
        this.trueOnNull = trueOnNull;
    }

    public Object getValue() {
        return value;
    }

    public Condition getCondition() {
        return condition;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isTrueOnNull() {
        return trueOnNull;
    }
}
