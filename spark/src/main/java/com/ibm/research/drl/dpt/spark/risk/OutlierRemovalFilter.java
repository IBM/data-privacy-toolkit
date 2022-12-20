/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OutlierRemovalFilter {
    private final List<String> identityFields;
    private final List<ThresholdCondition> thresholds;

    public List<String> getIdentityFields() {
        return identityFields;
    }

    public List<ThresholdCondition> getThresholds() {
        return thresholds;
    }

    @JsonCreator
    public OutlierRemovalFilter(
            @JsonProperty(value = "identityFields", required = true) final List<String> identityFields,
            @JsonProperty(value = "thresholds", required = true) final List<ThresholdCondition> thresholds
    ) {
        this.identityFields = identityFields;
        this.thresholds = thresholds;
    }
}
