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
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.commons.math3.distribution.HypergeometricDistribution;

import java.util.List;
import java.util.Map;


public class HolohanRiskMetric implements RiskMetric {
    public static final String POPULATION = "N";
    private int N;
    private List<Partition> equivalenceClassesOverAnonymizedDataset;
    private int n;

    @Override
    public String getName() {
        return "HolohanRiskMetric";
    }

    @Override
    public String getShortName() {
        return "HRM";
    }

    @Override
    public double report() {
        double datasetRisk = 0.0;

        for (final Partition equivalenceClass : equivalenceClassesOverAnonymizedDataset) {
            int equivalenceClassSize = equivalenceClass.size();

            int populationUpperLimit = N - n + equivalenceClassSize;
            int numberOfTries = populationUpperLimit - equivalenceClassSize + 1;

            double[] individualProbabilities = new double[numberOfTries];

            double probabilityPerEquivalenceClass = 0.0;
            for (int h = equivalenceClassSize; h <= populationUpperLimit; ++h) {
                HypergeometricDistribution distributionOfEquivalenceClassI = new HypergeometricDistribution(N, h, n);

                double individualProbability = distributionOfEquivalenceClassI.probability(equivalenceClassSize);

                individualProbabilities[h - equivalenceClassSize] = individualProbability;

                probabilityPerEquivalenceClass += individualProbability;
            }

            double riskOfEquivalenceClass = 0.0;
            for (int i = 0; i < individualProbabilities.length; ++i) {
                individualProbabilities[i] /= probabilityPerEquivalenceClass;
                individualProbabilities[i] /= (equivalenceClassSize + i);

                riskOfEquivalenceClass += individualProbabilities[i];
            }

            datasetRisk = Double.max(datasetRisk, riskOfEquivalenceClass);
        }

        return datasetRisk;
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformation, int k, Map<String, String> options) {
        this.N = Integer.parseInt(options.get(POPULATION));
        this.n = anonymized.getNumberOfRows();

        if (N < n)
            throw new IllegalArgumentException("Population parameter must be larger than the dataset (sample) size");

        this.equivalenceClassesOverAnonymizedDataset = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformation);

        return this;
    }

    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(POPULATION)) throw new IllegalArgumentException("Missing parameter POPULATION");

        String nString = null;
        try {
            nString = options.get(POPULATION);

            if (null == nString) throw new IllegalArgumentException("Missing parameter POPULATION");
            int N = Integer.parseInt(nString);

            if (0 >= N) throw new IllegalArgumentException("POPULATION must be greater than 0");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("POPULATION value is not a valid integer: " + nString);
        }
    }
}
