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

import java.util.List;
import java.util.Map;

public class KRatioMetric implements RiskMetric {
    public static final String GAMMA = "gamma";
    private List<Partition> partitions;
    private double gamma;

    @Override
    public String getName() {
        return "K Ratio Metric";
    }

    @Override
    public String getShortName() {
        return "KRM";
    }

    @Override
    public double report() {
        double risk = 0.0;

        for (Partition partition: partitions) {
            risk = Math.max(gamma / partition.size(), risk);
        }

        return risk;
    }

    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(GAMMA)) throw new IllegalArgumentException("Missing parameter gamma");
        String gammaString = null;
        try {
            gammaString = options.get(GAMMA);

            if (null == gammaString) throw new IllegalArgumentException("Missing parameter gamma");
            double gamma = Double.parseDouble(gammaString);

            if (gamma <= 0 || gamma > 1) throw new IllegalArgumentException("gamma value is not a valid real number in (0, 1] " + gammaString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("gamma value is not a valid real number in (0, 1] " + gammaString);
        }
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        this.partitions = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList);
        this.gamma = Double.parseDouble(options.get(GAMMA));
        return this;
    }
}
