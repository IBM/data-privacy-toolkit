/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;


import java.util.Collection;

public class EntropyUtilities {
    public static double calculateEntropy(Collection<Long> values, Long total) {

        double sum1 = 0d;
        for(Long counter: values) {
            double v = ((double)counter) / ((double) total);
            sum1 += v * Math.log(v);
        }

        return -sum1;
    }

    public static double calculateEntropy(Histogram histogram, int total) {
        return calculateEntropy(histogram.values(), (long)total);
    }
}
