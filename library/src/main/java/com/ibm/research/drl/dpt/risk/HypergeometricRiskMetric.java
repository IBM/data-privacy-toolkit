/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HypergeometricRiskMetric implements RiskMetric {
    public static final String N = "N";

    private List<PoissonDistribution> F;
    private List<Integer> f;
    private double n;
    private double _N;

    @Override
    public String getName(){
        return "Hypergeometric distribution based metric";
    }

    @Override
    public String getShortName() {
        return "HGEORM";
    }


    private int extractFk(int k) {
        int Fk;
        do {
            Fk = F.get(k).sample();
        } while (Fk <= 0.0);

        return Fk;
    }

    public double report() {
        double risk = 0.0;

        final double p = n / _N;

        for (int k = 0; k < F.size(); ++k) {
            final int Fk = extractFk(k);

            final HypergeometricDistribution distribution = new HypergeometricDistribution(Fk, f.get(k), f.get(k));

            double k_risk = distribution.probability(1);

            risk = FastMath.max(risk, k_risk);
        }

        return risk;
    }

    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(N)) throw new IllegalArgumentException("Missing parameter N");
        String nString = null;
        try {
            nString = options.get(N);

            if (null == nString) throw new IllegalArgumentException("Missing parameter N");
            int N = Integer.parseInt(nString);

            if (0 >= N) throw new IllegalArgumentException("N must be greater than 0");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("N value is not a valid integer: " + nString);
        }
    }


    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        List<Partition> partitions = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList);

        n = anonymized.getNumberOfRows();
        _N = Integer.parseInt(options.get(N));

        f = new ArrayList<>(partitions.size());
        F = new ArrayList<>(partitions.size());

        for (final Partition partition : partitions) {
            final double pi_k = partition.size() / n;

            f.add(partition.size());

            double probability = _N * pi_k;

            F.add(
                    new PoissonDistribution(probability)
            );
        }

        return this;
    }
}
