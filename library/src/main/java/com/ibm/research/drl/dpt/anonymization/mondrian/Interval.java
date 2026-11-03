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

import java.io.Serializable;

/** Represents a numeric interval [low, high] with an optional median. */
public class Interval implements Serializable {
    /** The lower bound. */
    private final double low;
    /** The upper bound. */
    private final double high;
    /** The median, or {@code null} if not set. */
    private final Double median;

    /**
     * Returns the lower bound.
     *
     * @return the low value
     */
    public double getLow() {
        return low;
    }

    /**
     * Returns the upper bound.
     *
     * @return the high value
     */
    public double getHigh() {
        return high;
    }

    /**
     * Returns the median, or {@code null} if not set.
     *
     * @return the median
     */
    public Double getMedian() {
        return median;
    }

    /**
     * Returns the range (high minus low).
     *
     * @return the range
     */
    public double getRange() {
        return high - low;
    }

    /**
     * Constructs an Interval with no median.
     *
     * @param low  the lower bound
     * @param high the upper bound
     */
    public Interval(Double low, Double high) {
        this(low, high, null);
    }

    /**
     * Constructs an Interval with an optional median.
     *
     * @param low    the lower bound
     * @param high   the upper bound
     * @param median the median, or {@code null}
     */
    public Interval(double low, double high, Double median) {
        this.low = low;
        this.high = high;
        this.median = median;
    }

    @Override
    public Interval clone() {
        return new Interval(this.low, this.high);
    }
}

