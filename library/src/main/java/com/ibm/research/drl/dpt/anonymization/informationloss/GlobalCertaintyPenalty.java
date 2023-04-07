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
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.List;

public class GlobalCertaintyPenalty implements InformationMetric {
    private List<Partition> partitions;
    private List<ColumnInformation> columnInformationList;
    private IPVDataset anonymized;

    private int numberOfQuasiColumns() {
        int s = 0;

        for (ColumnInformation c : columnInformationList) {
            if (c.getColumnType() == ColumnType.QUASI) {
                s += 1;
            }
        }

        return s;
    }

    private double getNCP(Partition p) {
        double gcp = 0.0;

        for (int i = 0; i < columnInformationList.size(); i++) {
            ColumnInformation columnInformation = columnInformationList.get(i);
            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                gcp += p.getNormalizedWidth(i);
            }
        }

        return gcp;
    }

    @Override
    public String getName() {
        return "Global Certainty Penalty (GCP)";
    }

    @Override
    public String getShortName() {
        return "GCP";
    }

    @Override
    public double getLowerBound() {
        return Double.NaN;
    }

    @Override
    public double getUpperBound() {
        return Double.NaN;
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
        return false;
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
        int totalRecords = anonymized.getNumberOfRows();
        int d = numberOfQuasiColumns();

        double gcp = 0.0;

        for (Partition p : partitions) {
            double ncp = getNCP(p);
            gcp += p.size() * ncp;
        }

        return gcp / ((double) d * (double) totalRecords);
    }

    private InformationLossResult reportForColumn(int columnIndex) {
        int totalRecords = anonymized.getNumberOfRows();
        double gcp = 0.0;
        for (Partition p : partitions) {
            gcp += p.size() * p.getNormalizedWidth(columnIndex);
        }

        return new InformationLossResult(gcp / ((double) totalRecords), -1.0, -1.0);
    }

    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {

        List<InformationLossResult> results = new ArrayList<>();
        int columnIndex = 0;

        for (ColumnInformation columnInformation : columnInformationList) {
            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                columnIndex++;
                continue;
            }

            InformationLossResult iloss = reportForColumn(columnIndex);

            results.add(iloss);
            columnIndex++;
        }

        return results;
    }

    /**
     * Instantiates a new Global certainty penalty.
     *
     * @param original              the original
     * @param anonymized            the anonymized
     * @param columnInformationList the column information list
     */
    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;
        this.partitions = originalPartitions;
        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, options);
    }
}
