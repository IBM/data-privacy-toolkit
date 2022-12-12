/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

public class MedianInformation {
    private final Double low;
    private final Double high;
    private final String splitValueString;
    private final Double splitValueNumerical;

    public Double getSplitValueNumerical() {
        return splitValueNumerical;
    }

    /**
     * Gets split value.
     *
     * @return the split value
     */
    public String getSplitValueString() {
        return splitValueString;
    }

    /**
     * Gets low.
     *
     * @return the low
     */
    public Double getLow() {
        return low;
    }

    /**
     * Gets high.
     *
     * @return the high
     */
    public Double getHigh() {
        return high;
    }

    /**
     * Instantiates a new Median information.
     *
     * @param low        the low
     * @param high       the high
     * @param splitValue the split value
     */
    public MedianInformation(Double low, Double high, String splitValue) {
        this.low = low;
        this.high = high;
        this.splitValueString = splitValue;
        this.splitValueNumerical = null;
    }
    
    public MedianInformation(Double low, Double high, Double splitValue) {
        this.low = low;
        this.high = high;
        this.splitValueString = null;
        this.splitValueNumerical = splitValue;
    }
    
}

