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
import com.ibm.research.drl.dpt.anonymization.SensitiveColumnInformation;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.*;


public class SensitiveSimilarityMeasure implements InformationMetric {
    private Collection<Integer> sensitiveFields;
    private Map<String, Double> globalDistributions;
    private Collection<Map<String, Double>> partitionDistributions;

    @Override
    public String getName() {
        return "SensitiveSimilarityMeasure";
    }

    @Override
    public String getShortName() {
        return "SSM";
    }

    @Override
    public double getLowerBound() {
        return 0.0;
    }

    @Override
    public double getUpperBound() {
        return Double.MAX_VALUE;
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

    @Override
    public double report() {
        return computeMSE();
    }

    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {
        return null;
    }

    private Double computeMSE() {
        double maximumMSE = 0.0;

        for (Map<String, Double> localDistribution : partitionDistributions) {
            double MSE = 0.0;

            for (Map.Entry<String, Double> knownValues : globalDistributions.entrySet()) {
                final Double local = localDistribution.get(knownValues.getKey());

                MSE += Math.abs(knownValues.getValue() - (null == local ? 0.0 : local));
            }

            MSE /= globalDistributions.size();

            maximumMSE = Math.max(MSE, maximumMSE);
        }

        return maximumMSE;
    }

    private String combineSensitiveFields(List<String> row) {
        StringBuilder builder = new StringBuilder();

        for (Integer fieldId : this.sensitiveFields) {
            builder.append(',');
            builder.append(row.get(fieldId));
        }

        return builder.toString();
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.sensitiveFields = extractSensitiveFields(columnInformationList);
        this.globalDistributions = computeDistributionOfSensitiveValues(original);
        this.partitionDistributions = new ArrayList<>();

        for (Partition partition : anonymizedPartitions) { /* TODO: verify */
            partitionDistributions.add(computeDistributionOfSensitiveValues(partition.getMember()));
        }

        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, options);
    }

    private Map<String, Double> computeDistributionOfSensitiveValues(IPVDataset dataset) {
        Map<String, Double> distribution = new HashMap<>();

        for (List<String> row : dataset) {
            String sensitiveValues = combineSensitiveFields(row);

            Double oldCount = distribution.get(sensitiveValues);

            distribution.put(sensitiveValues, (null == oldCount ? 0 : oldCount) + 1);
        }

        for (Map.Entry<String, Double> entry : distribution.entrySet()) {
            entry.setValue(entry.getValue() / dataset.getNumberOfRows());
        }

        return distribution;
    }

    private Collection<Integer> extractSensitiveFields(List<ColumnInformation> columnInformationList) {
        List<Integer> sensitiveFields = new ArrayList<>();

        for (int i = 0; i < columnInformationList.size(); ++i) {
            if (columnInformationList.get(i) instanceof SensitiveColumnInformation) sensitiveFields.add(i);
        }

        return sensitiveFields;
    }
}
