/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.sampling;


import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

public class SamplingOptions implements AnonymizationAlgorithmOptions {

    private final double percentage;

    public double getPercentage() {
        return percentage;
    }

    @Override
    public int getIntValue(String optionName) {
        return 0;
    }

    @Override
    public String getStringValue(String optionName) {
        return null;
    }

    public SamplingOptions(double percentage) {
        this.percentage = percentage;
    }
}

