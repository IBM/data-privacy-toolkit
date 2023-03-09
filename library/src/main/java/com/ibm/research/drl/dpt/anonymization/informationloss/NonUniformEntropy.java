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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NonUniformEntropy implements InformationMetric {
    /* Original paper: https://www.openu.ac.il/lists/mediaserver_documents/personalsites/tamirtassa/entropy_j.pdf */
    /* paper that shows how NUE is a good metric for health data: http://ebooks.iospress.nl/publication/48242 */

    private IPVDataset original;
    private IPVDataset anonymized;
    private List<ColumnInformation> columnInformationList;
    private List<Map<String, Double>> proportionsOriginal;
    private List<Map<String, Double>> proprtionsAnonymized;
    private boolean withTransformationLevels;
    private int[] transformationLevels;
    private double globalMaximumEntropy;

    private List<Partition> originalPartitions;
    private List<Partition> anonymizedPartitions;

    private String createKey(String value) {
        return value;
    }

    @Override
    public String getName() {
        return "Non-Uniform entropy";
    }

    @Override
    public String getShortName() {
        return "NUE";
    }

    @Override
    public double getLowerBound() {
        return 0.0;
    }

    @Override
    public double getUpperBound() {
        return this.globalMaximumEntropy;
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
        return true;
    }

    public static double entropyForOutlier(Partition originalPartition, Map<String, Double> originalFrequencies,
                                           int columnIndex) {

        IPVDataset members = originalPartition.getMember();
        int numberOfRows = members.getNumberOfRows();

        double result = 0.0;

        for (int i = 0; i < numberOfRows; i++) {
            String originalValue = members.get(i, columnIndex);
            double proportion = originalFrequencies.get(originalValue);

            result += -log2(proportion);
        }

        return result;
    }

    public static double entropyForAnonymous(Partition originalPartition, Map<String, Double> originalFrequencies,
                                             Partition anonymizedPartition, Map<String, Double> anonymizedFrequencies,
                                             int columnIndex) {

        IPVDataset originalMembers = originalPartition.getMember();
        IPVDataset anonymizedMembers = anonymizedPartition.getMember();

        int numberOfRows = originalMembers.getNumberOfRows();

        double result = 0.0;

        for (int i = 0; i < numberOfRows; i++) {
            String originalValue = originalMembers.get(i, columnIndex);
            String anonymizedValue = anonymizedMembers.get(i, columnIndex);

            double freqOrig = originalFrequencies.get(originalValue);
            double freqAnon = anonymizedFrequencies.get(anonymizedValue);

            result += -log2(freqOrig / freqAnon);
        }


        return result;
    }

    private InformationLossResult reportForColumn(int columnIndex) {
        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        double weight = columnInformation.getWeight();

        if (columnInformation.getColumnType() != ColumnType.QUASI) {
            return null;
        }

        double result = 0.0;

        int numPartitions = originalPartitions.size();

        Map<String, Double> originalFrequencies = this.proportionsOriginal.get(columnIndex);
        Map<String, Double> anonymizedFrequencies = this.proprtionsAnonymized.get(columnIndex);

        for (int i = 0; i < numPartitions; i++) {
            Partition originalPartition = originalPartitions.get(i);

            if (originalPartition.isAnonymous()) {
                Partition anonymizedPartition = anonymizedPartitions.get(i);
                result += entropyForAnonymous(originalPartition, originalFrequencies,
                        anonymizedPartition, anonymizedFrequencies, columnIndex);
            } else {
                result += entropyForOutlier(originalPartition, originalFrequencies, columnIndex);
            }
        }

        double maxNUE = calculateMaxEntropy(originalFrequencies, columnIndex);

        return new InformationLossResult(weight * result, 0.0, maxNUE);
    }

    private double calculateMaxEntropyForEntireDataset() {

        double sum = 0.0;
        for (int columnIndex = 0; columnIndex < this.columnInformationList.size(); columnIndex++) {
            ColumnInformation columnInformation = columnInformationList.get(columnIndex);

            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                continue;
            }

            Map<String, Double> originalFrequencies = this.proportionsOriginal.get(columnIndex);
            double columnMax = calculateMaxEntropy(originalFrequencies, columnIndex);

            sum += columnMax;
        }

        return sum;
    }

    private double calculateMaxEntropy(Map<String, Double> originalFrequencies, int columnIndex) {

        double sum = 0.0;
        int numPartitions = originalPartitions.size();

        for (int i = 0; i < numPartitions; i++) {
            Partition originalPartition = originalPartitions.get(i);
            sum += entropyForOutlier(originalPartition, originalFrequencies, columnIndex);
        }

        return sum;
    }

    @Override
    public double report() {
        int numberOfColumns = anonymized.getNumberOfColumns();

        double result = 0.0;

        for (int j = 0; j < numberOfColumns; j++) {
            if (columnInformationList.get(j).getColumnType() != ColumnType.QUASI) {
                continue;
            }

            InformationLossResult columnResult = reportForColumn(j);
            result += columnResult.getValue();
        }

        return result;
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

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private Map<String, Double> calculateFrequenciesForColumn(IPVDataset dataset, int columnIndex) {
        int numberOfRows = dataset.getNumberOfRows();

        Map<String, Double> map = new HashMap<>();

        for (int i = 0; i < numberOfRows; i++) {

            String value = createKey(dataset.get(i, columnIndex));

            Double counter = map.get(value);
            if (counter == null) {
                map.put(value, 1.0);
            } else {
                map.put(value, counter + 1.0);
            }
        }

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            map.put(key, value / (double) numberOfRows);
        }
        return map;
    }

    private List<Map<String, Double>> calculateFrequencies(IPVDataset dataset) {

        List<Map<String, Double>> frequencies = new ArrayList<>();
        int numberOfColumns = dataset.getNumberOfColumns();

        for (int i = 0; i < numberOfColumns; i++) {
            if (columnInformationList.get(i).getColumnType() != ColumnType.QUASI) {
                frequencies.add(null);
            } else {
                frequencies.add(calculateFrequenciesForColumn(dataset, i));
            }
        }

        return frequencies;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;

        this.proportionsOriginal = calculateFrequencies(original);
        this.proprtionsAnonymized = calculateFrequencies(anonymized);

        this.originalPartitions = originalPartitions;
        this.anonymizedPartitions = anonymizedPartitions;

        this.withTransformationLevels = false;
        this.transformationLevels = null;

        this.globalMaximumEntropy = calculateMaxEntropyForEntireDataset();

        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized,
                                        List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;

        this.proportionsOriginal = calculateFrequencies(original);
        this.proprtionsAnonymized = calculateFrequencies(anonymized);

        this.originalPartitions = originalPartitions;
        this.anonymizedPartitions = anonymizedPartitions;

        this.withTransformationLevels = true;
        this.transformationLevels = transformationLevels;

        this.globalMaximumEntropy = calculateMaxEntropyForEntireDataset();

        return this;
    }

}

