/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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

