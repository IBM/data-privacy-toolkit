/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.schema.IdentifiedType;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IdentificationReport {
    private final Map<String, List<IdentifiedType>> rawResults;
    private final Map<String, IdentifiedType> bestTypes;
    private final long recordCount;

    @JsonCreator
    public IdentificationReport(
            @JsonProperty("rawResults")
            Map<String, List<IdentifiedType>> rawResults,
            @JsonProperty("bestTypes")
            Map<String, IdentifiedType> bestTypes,
            @JsonProperty("recordCount")
            long recordCount) {
        this.rawResults = rawResults;
        this.bestTypes = bestTypes;
        this.recordCount = recordCount;
    }

    public Map<String, List<IdentifiedType>> getRawResults() {
        return rawResults;
    }

    public Map<String, IdentifiedType> getBestTypes() {
        return bestTypes;
    }

    public long getRecordCount() {
        return recordCount;
    }
}
