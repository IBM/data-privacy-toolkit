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
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ContentRequirements;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctLDiversity implements PrivacyConstraint {
    private final int l;

    @JsonCreator
    public DistinctLDiversity(@JsonProperty("l") int l) {
        this.l = l;
    }

    public int getL() {
        return l;
    }

    private boolean checkDistinctLDiversity(Partition partition, List<Integer> sensitiveColumns) {
        for (Integer sensitiveColumn : sensitiveColumns) {
            Set<String> uniqueValues = new HashSet<>();

            IPVDataset members = partition.getMember();
            int numberOfRows = members.getNumberOfRows();

            for (int i = 0; i < numberOfRows; i++) {
                String value = members.get(i, sensitiveColumn);
                uniqueValues.add(value.toUpperCase());

                //break early if we have l-represented values
                if (uniqueValues.size() >= l) {
                    break;
                }
            }

            if (uniqueValues.size() < this.l) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        LDiversityMetric lDiversityMetric = (LDiversityMetric) metric;

        List<Histogram> histograms = lDiversityMetric.getHistograms();

        for (Histogram histogram : histograms) {
            if (histogram.size() < this.l) {
                return false;
            }
        }

        return true;
    }

    public boolean check(Partition partition, List<Integer> sensitiveColumns) {
        if (sensitiveColumns == null) {
            return true;
        }

        return checkDistinctLDiversity(partition, sensitiveColumns);

    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return false;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.SENSITIVE;
    }

    @Override
    public void sanityChecks(IPVDataset originalDataset) {
        if (this.l > originalDataset.getNumberOfRows()) {
            throw new RuntimeException("l-value is bigger than the size of original dataset");
        }
    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }

    @Override
    public PrivacyMetric getMetricInstance() {
        return new LDiversityMetric();
    }


    public String toString() {
        return "Distinct L-Diversity constraint, l value = " + l;
    }

}
