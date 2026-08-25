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
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

/**
 * Interface for information loss metrics used to evaluate anonymization quality.
 */
public interface InformationMetric {

    /**
     * Returns the name of this metric.
     *
     * @return the metric name
     */
    String getName();

    /**
     * Returns a short identifier for this metric.
     *
     * @return the short name
     */
    String getShortName();

    /**
     * Returns the theoretical lower bound of this metric.
     *
     * @return lower bound
     */
    double getLowerBound();

    /**
     * Returns the theoretical upper bound of this metric.
     *
     * @return upper bound
     */
    double getUpperBound();

    /**
     * Returns whether this metric supports numerical quasi-identifiers.
     *
     * @return true if numerical QIs are supported
     */
    boolean supportsNumerical();

    /**
     * Returns whether this metric supports categorical quasi-identifiers.
     *
     * @return true if categorical QIs are supported
     */
    boolean supportsCategorical();

    /**
     * Returns whether this metric supports datasets with suppressed records.
     *
     * @return true if suppressed datasets are supported
     */
    boolean supportsSuppressedDatasets();

    /**
     * Returns whether this metric supports column weights.
     *
     * @return true if weights are supported
     */
    boolean supportsWeights();

    /**
     * Report double.
     *
     * @return the double
     */
    double report();

    /**
     * Reports the information loss per quasi-identifier column.
     *
     * @return list of per-column information loss results
     */
    List<InformationLossResult> reportPerQuasiColumn();

    /**
     * Initializes this metric with the given datasets and partitions.
     *
     * @param original              the original dataset
     * @param anonymized            the anonymized dataset
     * @param originalPartitions    the original partitions
     * @param anonymizedPartitions  the anonymized partitions
     * @param columnInformationList the column information list
     * @param options               the metric options
     * @return this initialized metric
     */
    InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                 List<ColumnInformation> columnInformationList, InformationMetricOptions options);

    /**
     * Initializes this metric with transformation levels.
     *
     * @param original               the original dataset
     * @param anonymized             the anonymized dataset
     * @param originalPartitions     the original partitions
     * @param anonymizedPartitions   the anonymized partitions
     * @param columnInformationList  the column information list
     * @param transformationLevels   the transformation levels per column
     * @param options                the metric options
     * @return this initialized metric
     */
    InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                 List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options);
}
