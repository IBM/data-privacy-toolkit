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

        for(Map.Entry<Integer, Integer> entry: this.equivalenceClassSizes.entrySet()) {
            Integer size = entry.getKey();
            Integer count = entry.getValue();

            HypergeometricDistribution d = new HypergeometricDistribution(this.N, size, this.n);
            sum += (count / (double) totalEquivalenceClasses) * d.probability(1);
        }

        HypergeometricDistribution d = new HypergeometricDistribution(this.N, 1, this.n);
        double p = (((double)classesWithSizeOne / (double)totalEquivalenceClasses) * d.probability(1)) / sum;

        double estimatedUniques = (p * (double)classesWithSizeOne) / ((double) n / (double) N); 
        
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
