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
package com.ibm.research.drl.dpt.anonymization.informationloss;

/** Holds the information-loss metric value and its lower/upper bound. */
public class InformationLossResult {
    private final double value;
    private final double lowerBound;
    private final double upperBound;

    /**
     * Returns the metric value.
     *
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the lower bound.
     *
     * @return the lower bound
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the upper bound.
     *
     * @return the upper bound
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Constructs an InformationLossResult.
     *
     * @param value      the metric value
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public InformationLossResult(double value, double lowerBound, double upperBound) {
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "[" + lowerBound + ", " + value + ", " + upperBound + "]";
    }
}

