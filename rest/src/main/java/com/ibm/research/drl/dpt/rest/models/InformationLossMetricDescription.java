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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class InformationLossMetricDescription {
    private final String name;
    private final String shortName;
    private final boolean supportsSuppressedDatasets;

    public InformationLossMetricDescription(@JsonProperty("name") final String name,
                                            @JsonProperty("shortName") final String shortName,
                                            @JsonProperty("supportsSuppressedDatasets") final boolean supportsSuppressedDatasets) {
        this.name = name;
        this.shortName = shortName;
        this.supportsSuppressedDatasets = supportsSuppressedDatasets;
    }

}
