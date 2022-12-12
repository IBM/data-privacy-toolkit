/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public interface InformationMetric {

    String getName();
    String getShortName();

    double getLowerBound();
    double getUpperBound();

    boolean supportsNumerical();
    boolean supportsCategorical();
    boolean supportsSuppressedDatasets();

    boolean supportsWeights();

    /**
     * Report double.
     *
     * @return the double
     */
    double report();

    List<InformationLossResult> reportPerQuasiColumn();

    InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                 List<ColumnInformation> columnInformationList, InformationMetricOptions options);
    
    InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                 List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options);
}
