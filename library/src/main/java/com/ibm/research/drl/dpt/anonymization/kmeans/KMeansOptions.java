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
package com.ibm.research.drl.dpt.anonymization.kmeans;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

/**
 * Options for the {@link KMeansAnonymization} algorithm.
 */
public class KMeansOptions implements AnonymizationAlgorithmOptions {
    private final double suppressionRate;
    private final StrategyOptions strategy;

    /**
     * Returns the suppression rate.
     *
     * @return the suppression rate
     */
    public double getSuppressionRate() {
        return suppressionRate;
    }

    /**
     * Returns the reassignment strategy to use when a cluster is too small.
     *
     * @return the strategy options
     */
    public StrategyOptions getStrategy() {
        return strategy;
    }

    /**
     * Constructs a KMeansOptions instance.
     *
     * @param suppressionRate the suppression rate (percentage of rows that may be suppressed)
     * @param strategy        the reassignment strategy for under-sized clusters
     */
    public KMeansOptions(double suppressionRate, StrategyOptions strategy) {
        this.suppressionRate = suppressionRate;
        this.strategy = strategy;
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

