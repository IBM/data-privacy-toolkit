/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;


import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public abstract class StrategyImpl {
    private final IPVDataset original;
    private final double suppressionRate;
    private final List<ColumnInformation> columnInformationList;
    private final List<PrivacyConstraint> privacyConstraints;

    public StrategyImpl(IPVDataset original,
                        double suppressionRate,
                        List<ColumnInformation> columnInformationList,
                        List<PrivacyConstraint> privacyConstraints) {
        this.original = original;
        this.suppressionRate = suppressionRate;
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
    }

    public abstract IPVDataset buildAnonymizedDataset(List<KMeansCluster> clusters);

    public abstract List<Partition> getOriginalPartitions();

    public abstract List<Partition> getAnonymizedPartitions();

    public abstract Long getSuppressedRows();
}

