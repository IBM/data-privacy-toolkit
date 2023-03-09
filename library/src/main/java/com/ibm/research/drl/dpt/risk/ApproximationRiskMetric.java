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
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;


public class ApproximationRiskMetric implements RiskMetric {
    public static final String POPULATION = "N";
    public static final String USE_GLOBAL_P = "useGlobalP";

    private List<PoissonDistribution> F;
    private List<Integer> f;
    private double n;
    private double N;
    private boolean useGlobalP;

    @Override
    public String getName() {
        return "Approximation based risk metric";
    }

    @Override
    public String getShortName() {
        return "BINOM";
    }

    private int extractFk(int k) {
        int Fk;
        do {
            Fk = F.get(k).sample();
        } while (Fk <= 0.0);

        return Fk;
    }

    @Override
    public double report() {
        if (useGlobalP) {
            return reportGlobalP();
        }

        return reportLocalP();
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

        if (!options.containsKey(USE_GLOBAL_P)) throw new IllegalArgumentException("Missing parameter useGlobalP");
        String useGlobalP = options.get(USE_GLOBAL_P);

        if (null == useGlobalP) throw new IllegalArgumentException("Missing parameter useGlobalP");
        boolean bool = Boolean.parseBoolean(useGlobalP);
    }


    private double calculateEQRisk(double fk, double pk) {
        double k_risk = 1.0;
        double qk = 1 - pk;

        for (int i = 1; i <= 7; ++i) {
            double v1 = factorial(i) * Math.pow(qk, i);

            double p = 1.0;
            for (int j = 1; j <= i; j++) {
                p = p * (fk + j);
            }

            k_risk += v1 / p;
        }

        return (pk / fk) * k_risk;
    }

    public Double reportLocalP() {
        double risk = 0.0;

        for (int k = 0; k < F.size(); ++k) {
            final int Fk = extractFk(k);
            final double fk = f.get(k);

            double pk = (double) f.get(k) / (double) Fk;

            double k_risk = calculateEQRisk(fk, pk);

            risk = FastMath.max(risk, k_risk);
        }

        return risk;
    }

    public double reportGlobalP() {
        double risk = 0.0;

        final double p = n / N;

        for (int k = 0; k < F.size(); ++k) {
            double k_risk = calculateEQRisk(f.get(k), p);
            risk = FastMath.max(risk, k_risk);
        }

        return risk;
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        List<Partition> partitions = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList);
        n = anonymized.getNumberOfRows();
        N = Integer.parseInt(options.get(POPULATION));
        useGlobalP = Boolean.parseBoolean(options.get(USE_GLOBAL_P));

        f = new ArrayList<>(partitions.size());
        F = new ArrayList<>(partitions.size());

        for (final Partition partition : partitions) {
            final double pi_k = partition.size() / n;

            f.add(partition.size());

            F.add(
                    new PoissonDistribution(N * pi_k)
            );
        }


        return this;
    }
}
