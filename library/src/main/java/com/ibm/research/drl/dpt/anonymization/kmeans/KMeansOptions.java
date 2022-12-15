/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

public class KMeansOptions implements AnonymizationAlgorithmOptions {
    private final double suppressionRate;
    private final StrategyOptions strategy;

    /**
     * Gets suppression rate.
     *
     * @return the suppression rate
     */
    public double getSuppressionRate() {
        return suppressionRate;
    }

    public StrategyOptions getStrategy() {
        return strategy;
    }

    public KMeansOptions(double suppressionRate, StrategyOptions strategy) {
        this.suppressionRate = suppressionRate;
        this.strategy = strategy;
    }

    @Override
    public int getIntValue(String optionName) {
        return 0;
    }

    @Override
    public String getStringValue(String optionName) {
        return null;
    }
}

