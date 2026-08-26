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
package com.ibm.research.drl.dpt.processors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.schema.IdentifiedType;

import java.util.List;
import java.util.Map;

/** Holds the results of a type identification pass over a dataset. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IdentificationReport {
    private final Map<String, List<IdentifiedType>> rawResults;
    private final Map<String, IdentifiedType> bestTypes;
    private final long recordCount;

    /**
     * Constructs an IdentificationReport.
     *
     * @param rawResults  map of field name to all identified types with their counts
     * @param bestTypes   map of field name to the single best-identified type
     * @param recordCount number of records processed
     */
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

    /**
     * Returns the raw identification results for all fields.
     *
     * @return map of field name to identified types
     */
    public Map<String, List<IdentifiedType>> getRawResults() {
        return rawResults;
    }

    /**
     * Returns the best-identified type for each field.
     *
     * @return map of field name to best identified type
     */
    public Map<String, IdentifiedType> getBestTypes() {
        return bestTypes;
    }

    /**
     * Returns the number of records that were processed.
     *
     * @return record count
     */
    public long getRecordCount() {
        return recordCount;
    }
}
