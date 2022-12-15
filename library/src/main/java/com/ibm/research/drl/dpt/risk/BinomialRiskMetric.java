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
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BinomialRiskMetric implements RiskMetric {
    public static final String POPULATION = "N";
    public static final String USE_GLOBAL_P = "useGlobalP";

    private List<PoissonDistribution> F;
    private List<Integer> f;
    private double n;
    private double N;
    private boolean useGlobalP;

    @Override
    public String getName() {
        return "Binomial distribution based risk metric";
    }

    @Override
    public String getShortName() {
        return "BINOM";
    }

    public static int extractFk(PoissonDistribution F) {
        int Fk;
        do {
            Fk = F.sample();
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

    public double reportLocalP() {
        double risk = 0.0;

        for (int k = 0; k < F.size(); ++k) {
            final int Fk = extractFk(F.get(k));
            final int tries = FastMath.min(10, f.get(k));

            double p = (double) f.get(k) / (double) Fk;

            double k_risk = risk(Fk, p, tries);

            risk = FastMath.max(risk, k_risk);
        }

        return risk;
    }

    public double reportGlobalP() {
        double risk = 0.0;

        final double p = n / N;

        for (int k = 0; k < F.size(); ++k) {
            final int Fk = extractFk(F.get(k));
            final int tries = FastMath.min(10, f.get(k));

            risk = FastMath.max(risk, risk(Fk, p, tries));
        }

        return risk;
    }

    public static double risk(int Fk, double p, long tries) {
        double k_risk = 0.0;
        final BinomialDistribution binomialDistribution = new BinomialDistribution(Fk, p);

        for (long i = 1L; i <= tries; ++i) {
            k_risk += Math.pow(binomialDistribution.probability(1), i);
        }

        return k_risk;
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
