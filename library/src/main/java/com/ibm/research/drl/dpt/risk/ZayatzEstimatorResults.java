/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

public class ZayatzEstimatorResults {
    
    private final double estimatedRealUniques;
    private final double uniques;
    
    public double getEstimatedRealUniques() {
        return estimatedRealUniques;
    }

    public double getUniques() {
        return uniques;
    }

    public ZayatzEstimatorResults(double uniques, double estimatedRealUniques) {
        this.uniques = uniques;
        this.estimatedRealUniques = estimatedRealUniques;
    }
    
}

