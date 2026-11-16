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
package com.ibm.research.drl.dpt.rest.models;

import java.util.Map;

/**
 * The outcome of anonymizing a dataset with one pair of parameters.
 *
 * @param k                the requested k
 * @param suppression      the maximum suppression requested, in percent
 * @param realSuppression  the suppression actually needed by the best generalization, in percent
 * @param bestNode         the generalization levels of the quasi-identifiers in the best lattice node
 * @param informationLoss  the information loss, by metric short name
 * @param riskMetricResults the re-identification risk, by metric short name
 */
public record ExplorationResult(int k,
                                double suppression,
                                double realSuppression,
                                String bestNode,
                                Map<String, InformationLossValue> informationLoss,
                                Map<String, Double> riskMetricResults) {
    /**
     * A value of an information loss metric.
     *
     * @param value the value of the metric
     */
    public record InformationLossValue(double value) {
    }
}
