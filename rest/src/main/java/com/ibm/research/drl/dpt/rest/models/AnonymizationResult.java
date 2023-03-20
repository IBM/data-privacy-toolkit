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
package com.ibm.research.drl.dpt.rest.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnonymizationResult {
    private final String dataset;
    private final InformationLossResultDescription informationLossResult;
    private final List<ColumnInformation> columnInformation;
    private final double suppression;

    @JsonCreator
    public AnonymizationResult(@JsonProperty("dataset") final String dataset,
                               @JsonProperty("informationLossResult") final InformationLossResultDescription informationLossResult,
                               @JsonProperty("columnInformation") List<ColumnInformation> columnInformation,
                               @JsonProperty("suppression") double suppression) {
        this.dataset = dataset;
        this.informationLossResult = informationLossResult;
        this.columnInformation = columnInformation;
        this.suppression = suppression;
    }


    public double getSuppression() {
        return suppression;
    }

    public List<ColumnInformation> getColumnInformation() {
        return columnInformation;
    }

    public InformationLossResultDescription getInformationLossResult() {
        return informationLossResult;
    }

    public String getDataset() {
        return dataset;
    }
}
