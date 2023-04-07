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

    @Override
    public Interval clone() {
        return new Interval(this.low, this.high);
    }
}

