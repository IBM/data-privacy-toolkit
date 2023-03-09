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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.anonymization.constraints.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "KAnonymity", value = KAnonymity.class),
        @JsonSubTypes.Type(name = "DistinctLDiversity", value = DistinctLDiversity.class),
        @JsonSubTypes.Type(name = "EntropyLDiversity", value = EntropyLDiversity.class),
        @JsonSubTypes.Type(name = "RecursiveCLDiversity", value = RecursiveCLDiversity.class),
        @JsonSubTypes.Type(name = "TCloseness", value = TCloseness.class),
})
public interface
PrivacyConstraint extends Serializable {
    boolean check(PrivacyMetric metric);

    boolean check(Partition partition, List<Integer> sensitiveColumns);

    boolean requiresAnonymizedPartition();

    int contentRequirements();

    void sanityChecks(IPVDataset dataset);

    void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList);

    @JsonIgnore
    PrivacyMetric getMetricInstance();
}
