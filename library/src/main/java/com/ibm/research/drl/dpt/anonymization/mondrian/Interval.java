/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

import java.io.Serializable;

public class Interval implements Serializable {
    private final double low;
    private final double high;
    private final Double median;
    
    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public Double getMedian() {
        return median;
    }

    public double getRange() {
        return high - low;
    }
    
    public Interval(Double low, Double high) {
        this(low, high, null);
    }
    
    public Interval(double low, double high, Double median) {
        this.low = low;
        this.high = high;
        this.median = median; 
    }
    
    public Interval clone() {
        return new Interval(this.low, this.high);
    }
}

