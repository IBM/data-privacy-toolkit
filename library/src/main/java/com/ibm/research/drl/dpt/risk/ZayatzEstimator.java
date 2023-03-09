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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ZayatzEstimator implements RiskMetric {
    public static final String POPULATION = "N";

    private int n;
    private int N;
    private int totalEquivalenceClasses;

    private Map<Integer, Integer> equivalenceClassSizes;

    @Override
    public String getName() {
        return "Zayatz Estimator";
    }

    @Override
    public String getShortName() {
        return "ZAYATZ";
    }

    @Override
    public double report() {
        ZayatzEstimatorResults results = reportUniqueness();
        return results.getEstimatedRealUniques();
    }

    public ZayatzEstimatorResults reportUniqueness() {
        double sum = 0.0;

        Integer classesWithSizeOne = this.equivalenceClassSizes.get(1);

        if (classesWithSizeOne == null) {
            return new ZayatzEstimatorResults(0.0, 0.0);
        }

        for (Map.Entry<Integer, Integer> entry : this.equivalenceClassSizes.entrySet()) {
            Integer size = entry.getKey();
            Integer count = entry.getValue();

            HypergeometricDistribution d = new HypergeometricDistribution(this.N, size, this.n);
            sum += (count / (double) totalEquivalenceClasses) * d.probability(1);
        }

        HypergeometricDistribution d = new HypergeometricDistribution(this.N, 1, this.n);
        double p = (((double) classesWithSizeOne / (double) totalEquivalenceClasses) * d.probability(1)) / sum;

        double estimatedUniques = (p * (double) classesWithSizeOne) / ((double) n / (double) N);

        return new ZayatzEstimatorResults(classesWithSizeOne, estimatedUniques);
    }


    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(POPULATION)) throw new IllegalArgumentException("Missing parameter N");
        String nString = null;
        try {
            nString = options.get(POPULATION);

            if (null == nString) throw new IllegalArgumentException("Missing parameter N");
            int N = Integer.parseInt(nString);

            if (0 >= N) throw new IllegalArgumentException("N must be greater than 0");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("N value is not a valid integer: " + nString);
        }
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        this.n = anonymized.getNumberOfRows();
        this.N = Integer.parseInt(options.get(POPULATION));

        this.equivalenceClassSizes = new HashMap<>();
        this.totalEquivalenceClasses = 0;

        for (final Partition partition : AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList)) {
            this.totalEquivalenceClasses += 1;

            final Integer size = partition.size();

            this.equivalenceClassSizes.merge(size, 1, Integer::sum);
        }

        return this;
    }
}
