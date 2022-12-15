/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
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


