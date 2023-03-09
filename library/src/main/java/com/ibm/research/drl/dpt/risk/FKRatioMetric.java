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
import org.apache.commons.math3.distribution.PoissonDistribution;

import java.util.List;
import java.util.Map;


public class FKRatioMetric implements RiskMetric {
    public static final String POPULATION = "population";
    private List<Partition> partitions;
    private double population;
    private double n;

    @Override
    public String getName() {
        return "FK Ratio Metric";
    }

    @Override
    public String getShortName() {
        return "FKRM";
    }

    private int extractFk(PoissonDistribution poissonDistribution) {
        int Fk;
        do {
            Fk = poissonDistribution.sample();
        } while (Fk <= 0.0);

        return Fk;
    }

    @Override
    public double report() {
        double risk = 0.0;

        for (Partition partition : partitions) {
            final double pi_k = partition.size() / n;

            PoissonDistribution poissonDistribution = new PoissonDistribution(population * pi_k);
            double Fk = extractFk(poissonDistribution);

            risk = Math.max(1.0 / Fk, risk);
        }

        return risk;
    }

    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(POPULATION)) throw new IllegalArgumentException("Missing parameter population");
        String populationString = null;
        try {
            populationString = options.get(POPULATION);

            if (null == populationString) throw new IllegalArgumentException("Missing parameter population");
            int N = Integer.parseInt(populationString);

            if (0 >= N) throw new IllegalArgumentException("population must be greater than 0");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("population value is not a valid integer: " + populationString);
        }
    }

    private double anonymizedSize(List<Partition> partitions) {
        double size = 0.0;

        for (Partition partition : partitions) {
            size += partition.size();
        }

        return size;
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        this.partitions = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList);
        this.population = Double.parseDouble(options.get(POPULATION));
        this.n = anonymizedSize(partitions);
        return this;
    }
}
