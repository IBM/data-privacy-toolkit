/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;


import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Histogram<T> extends HashMap<T, Long> {

    public Histogram() {
        super();
    }

    public void update(Histogram<T> other) {
        for (Map.Entry<T, Long> pair : other.entrySet()) {
            T key = pair.getKey();
            Long value = pair.getValue();

            if (!this.containsKey(key)) {
                this.put(key, value);
            } else {
                Long v = this.get(key);
                this.put(key, v + value);
            }
        }
    }

    public static <K> Histogram<K> createHistogram(List<K> values) {
        Histogram<K> histogram = new Histogram();

        for (K value : values) {

            Long counter = histogram.get(value);
            if (counter == null) {
                histogram.put(value, 1L);
            } else {
                histogram.put(value, counter + 1);
            }
        }

        return histogram;
    }

    public static Histogram<String> createHistogram(IPVDataset dataset, int column) {
        return createHistogram(dataset, column, false);
    }

    public static Histogram<String> createHistogram(IPVDataset dataset, int column, boolean toLowercase) {
        Histogram<String> histogram = new Histogram();

        for (int i = 0; i < dataset.getNumberOfRows(); i++) {
            String value = dataset.get(i, column);

            if (toLowercase) {
                value = value.toLowerCase();
            }

            Long counter = histogram.get(value);
            if (counter == null) {
                histogram.put(value, 1L);
            } else {
                histogram.put(value, counter + 1);
            }
        }

        return histogram;
    }


}
