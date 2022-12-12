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

import java.util.List;
import java.util.Map;

public class HolohanConfidenceRiskMetric implements RiskMetric {
    public static final String POPULATION = "N";
    public static final String CONFIDENCE = "HRMConfidence";
    private int N;
    private List<Partition> equivalenceClassesOverAnonymizedDataset;
    private int n;
    private double conf = 0.0;

    @Override
    public String getName() { return "HolohanConfidenceRiskMetric"; }

    @Override
    public String getShortName() {
        return "HCRM";
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
            double cumulativeProbability = 0.0;
            for (int i = 0; i < individualProbabilities.length; ++i) {
                individualProbabilities[i] /= probabilityPerEquivalenceClass;
                cumulativeProbability += individualProbabilities[i];

                if (cumulativeProbability > 1 - conf) {
                    riskOfEquivalenceClass = (equivalenceClassSize + i > 1) ? 1.0/(equivalenceClassSize + i - 1) : Double.POSITIVE_INFINITY;
                    break;
                }
            }

            datasetRisk = Double.max(datasetRisk, riskOfEquivalenceClass);
        }

        return datasetRisk;
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformation, int k, Map<String, String> options) {
        this.N = Integer.parseInt(options.get(POPULATION));
        this.n = anonymized.getNumberOfRows();
        if (options.containsKey(CONFIDENCE)) { this.conf = Double.parseDouble(options.get(CONFIDENCE)); }

        if (N < n) throw new IllegalArgumentException("Population parameter must be larger than the dataset (sample) size");

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

            if (options.containsKey(CONFIDENCE)) {
                conf = Double.parseDouble(options.get(CONFIDENCE));
                if (conf < 0 || conf > 1) throw new IllegalArgumentException("CONFIDENCE, when specified, must be between 0 and 1");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("POPULATION value is not a valid integer: " + nString);
        }
    }
}
