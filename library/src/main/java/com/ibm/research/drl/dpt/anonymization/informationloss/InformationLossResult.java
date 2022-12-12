/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

public class InformationLossResult {
    private final double value;
    private final double lowerBound;
    private final double upperBound;

    public double getValue() {
        return value;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public InformationLossResult(double value, double lowerBound, double upperBound) {
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public String toString() {
        return "[" + lowerBound + ", " + value + ", " + upperBound + "]";
    }
}

