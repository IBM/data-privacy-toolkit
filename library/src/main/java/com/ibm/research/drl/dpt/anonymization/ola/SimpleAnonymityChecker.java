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
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.informationloss.DiscernibilityStar;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SimpleAnonymityChecker implements AnonymityChecker {
    private final IPVDataset dataset;
    private final List<ColumnInformation> columnInformationList;
    private final List<Integer> sensitiveColumns;
    private final List<PrivacyConstraint> privacyConstraints;
    private final int privacyConstraintsContentRequirements;

    private boolean checkConstraints(Partition partition) {
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (!privacyConstraint.check(partition, sensitiveColumns)) {
                return false;
            }
        }

        return true;
    }

    public double calculateSuppressionRate(LatticeNode node) {
        List<Partition> partitions;

        int contentRequirements = this.privacyConstraintsContentRequirements;

        if (contentRequirements == ContentRequirements.NONE) {
            Map<String, Integer> eqCounters = DatasetGeneralizer.generalizeCSVAndCountEQ(dataset, columnInformationList, node.getValues());

            partitions = new ArrayList<>();
            for (Integer eqSize : eqCounters.values()) {
                partitions.add(new VirtualPartition(eqSize));
            }
        } else {
            Collection<Partition> partitionCollection =
                    DatasetGeneralizer.generalizeAndPartition(dataset, columnInformationList, node.getValues(), contentRequirements);
            partitions = new ArrayList<>(partitionCollection);
        }

        double suppressedRows = 0.0d;

        for (Partition partition : partitions) {
            if (!checkConstraints(partition)) {
                suppressedRows += partition.size();
                partition.setAnonymous(false);
            } else {
                partition.setAnonymous(true);
            }
        }

        /* DM has not content requirements so we are fine */
        InformationMetric metric = new DiscernibilityStar(); /*TODO: replace with NUE */
        metric.initialize(dataset, null, partitions, null, this.columnInformationList, null);
        node.setInformationLoss(metric.report());

        return 100.0 * (suppressedRows / (double) dataset.getNumberOfRows());
    }


    /**
     * Instantiates a new Anonymity checker.
     *
     * @param columnInformationList the column information list
     */
    public SimpleAnonymityChecker(IPVDataset dataset, List<ColumnInformation> columnInformationList, List<PrivacyConstraint> privacyConstraints) {
        this.dataset = dataset;
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.privacyConstraintsContentRequirements = AnonymizationUtils.buildPrivacyConstraintContentRequirements(this.privacyConstraints);
        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.SENSITIVE);
    }

}


