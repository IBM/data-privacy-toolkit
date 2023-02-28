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
package com.ibm.research.drl.dpt.anonymization.constraints;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public class KAnonymity implements PrivacyConstraint {
    private final int k;

    @JsonCreator
    public KAnonymity(
            @JsonProperty("k") int k
    ) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        return ((KAnonymityMetric) metric).getCount() >= this.k;
    }

    @Override
    public boolean check(Partition partition, List<Integer> sensitiveColumns) {
        return partition.size() >= k;
    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return false;
    }

    @Override
    public String toString() {
        return "K-anonymity constraint with k-value = " + k;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.NONE;
    }

    @Override
    public void sanityChecks(IPVDataset originalDataset) {
        if (this.k > originalDataset.getNumberOfRows()) {
            throw new RuntimeException("k-value is larger than the dataset");
        }
    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }

    @Override
    public PrivacyMetric getMetricInstance() {
        return new KAnonymityMetric();
    }
}
