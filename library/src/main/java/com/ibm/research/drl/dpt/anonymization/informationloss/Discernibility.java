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

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.List;

public class Discernibility implements InformationMetric {
    private List<Partition> partitions;
    private IPVDataset original;
    private int quasiIdentifiersLength;

    @Override
    public String getName() {
        return "Discernibility";
    }

    @Override
    public String getShortName() {
        return "DM";
    }

    @Override
    public double getLowerBound() {
        double value = 0.0;

        for (Partition p : partitions) {
            int pSize = p.size();

            if (pSize > 0) {
                value += Math.pow(pSize, 2);
            }
        }

        return value;
    }

    @Override
    public double getUpperBound() {
        return Math.pow(original.getNumberOfRows(), 2.0);
    }

    @Override
    public boolean supportsNumerical() {
        return true;
    }

    @Override
    public boolean supportsCategorical() {
        return true;
    }

    @Override
    public boolean supportsSuppressedDatasets() {
        return true;
    }

    @Override
    public boolean supportsWeights() {
        return false;
    }

    /**
     * Report double.
     *
     * @return the double
     */
    @Override
    public double report() {
        double value = 0.0;

        int totalRows = this.original.getNumberOfRows();

        for (Partition p : partitions) {
            int pSize = p.size();

            if (p.isAnonymous()) {
                value += Math.pow(pSize, 2);
            } else {
                value += pSize * totalRows;
            }
        }

        return value;
    }

    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {
        double iloss = report();
        double lower = getLowerBound();
        double upper = getUpperBound();

        List<InformationLossResult> results = new ArrayList<>();

        for (int i = 0; i < quasiIdentifiersLength; i++) {
            results.add(new InformationLossResult(iloss, lower, upper));
        }

        return results;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.partitions = originalPartitions;
        this.original = original;
        this.quasiIdentifiersLength = AnonymizationUtils.countColumnsByType(columnInformationList, ColumnType.QUASI);
        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, options);
    }
}
