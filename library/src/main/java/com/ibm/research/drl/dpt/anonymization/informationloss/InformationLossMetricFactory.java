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


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class InformationLossMetricFactory {
    private static final List<InformationMetric> metricList = Arrays.asList(
            new AverageEquivalenceClassSize(),
            new CategoricalPrecision(),
            new Discernibility(),
            new DiscernibilityStar(),
            new GeneralizedLossMetric(),
            new GlobalCertaintyPenalty(),
            new NonUniformEntropy(),
            new SensitiveSimilarityMeasure()
    );

    public static List<InformationMetric> getAvailableMetrics() {
        return metricList;
    }

    public static InformationMetric getInstance(String shortName) {
        for (InformationMetric metric : metricList) {
            if (metric.getShortName().equals(shortName)) {
                try {
                    return metric.getClass().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}

