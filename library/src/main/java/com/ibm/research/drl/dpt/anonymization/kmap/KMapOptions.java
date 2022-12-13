/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmap;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

public class KMapOptions implements AnonymizationAlgorithmOptions {
    private final double suppressionRate;

    public KMapOptions(double s) {
        this.suppressionRate = s;
    }

    public double getSuppressionRate() {
        return suppressionRate;
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

