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
package com.ibm.research.drl.dpt.anonymization.kmap;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

/**
 * Options for the {@link KMap} anonymization algorithm.
 */
public class KMapOptions implements AnonymizationAlgorithmOptions {
    private final double suppressionRate;

    /**
     * Constructs a KMapOptions with the given suppression rate.
     *
     * @param s the suppression rate (percentage of rows that may be suppressed)
     */
    public KMapOptions(double s) {
        this.suppressionRate = s;
    }

    /**
     * Returns the suppression rate.
     *
     * @return the suppression rate
     */
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

