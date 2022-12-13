/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
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

