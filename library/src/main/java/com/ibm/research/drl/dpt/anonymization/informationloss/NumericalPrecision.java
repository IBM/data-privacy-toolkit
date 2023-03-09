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

public class NumericalPrecision implements InformationMetric {
    private IPVDataset original;
    private IPVDataset anonymized;
    private List<ColumnInformation> columnInformationList;

    @Override
    public String getName() {
        return "Numerical Precision";
    }

    @Override
    public String getShortName() {
        return "NP";
    }

    @Override
    public double getLowerBound() {
        return 0.0d;
    }

    @Override
    public double getUpperBound() {
        return 1.0d;
    }

    @Override
    public boolean supportsNumerical() {
        return true;
    }

    @Override
    public boolean supportsCategorical() {
        return false;
    }

    @Override
    public boolean supportsSuppressedDatasets() {
        return false;
    }

    @Override
    public boolean supportsWeights() {
        return false;
    }

    @Override
    public double report() {
        List<InformationLossResult> columnResults = reportPerQuasiColumn();

        double sum = 0.0;
        for (InformationLossResult lossResult : columnResults) {
            sum += lossResult.getValue();
        }

        return sum / (double) columnResults.size();
    }

    private InformationLossResult reportForColumn(int columnIndex) {
        ColumnInformation columnInformation = columnInformationList.get(columnIndex);

        if (columnInformation.getColumnType() != ColumnType.QUASI) {
            return null;
        }

        if (columnInformation.isCategorical()) {
            return null;
        }

        int numberOfRows = anonymized.getNumberOfRows();
        double globalRange = getGlobalRange(original, columnIndex);
        double precision = 0.0;

        for (int i = 0; i < numberOfRows; i++) {
            String interval = anonymized.get(i, columnIndex);
            double range = intervalToRange(interval);

            precision += range;
        }

        double iloss = precision / numberOfRows / globalRange;
        return new InformationLossResult(iloss, 0.0d, 1.0d);
    }

    private double intervalToRange(String interval) {
        String[] extremes = interval.split("-");

        if (extremes.length == 0) {
            throw new IllegalArgumentException("Interval is empty");
        }

        if (extremes.length == 1) {
            return 0.0d;
        }

        return Double.parseDouble(extremes[1]) - Double.parseDouble(extremes[0]);
    }

    private double getGlobalRange(IPVDataset original, int columnIndex) {
        double globalMin = Double.POSITIVE_INFINITY;
        double globalMax = Double.NEGATIVE_INFINITY;

        for (List<String> row : original) {
            double value = Double.parseDouble(row.get(columnIndex));

            globalMax = Math.max(globalMax, value);
            globalMin = Math.min(globalMin, value);
        }

        return globalMax - globalMin;
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

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;

        if (anonymized.getNumberOfRows() != original.getNumberOfRows()) {
            return null;
        }

        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions, List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return null;
    }
}

