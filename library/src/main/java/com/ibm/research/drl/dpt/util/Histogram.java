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
package com.ibm.research.drl.dpt.util;


import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A map from values to their occurrence counts.
 *
 * @param <T> the type of values counted
 */
public class Histogram<T> extends HashMap<T, Long> {

    /** Constructs an empty Histogram. */
    public Histogram() {
        super();
    }

    /**
     * Merges another histogram into this one by summing counts.
     *
     * @param other the histogram to merge in
     */
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

    /**
     * Creates a histogram from a list of values.
     *
     * @param <K>    the value type
     * @param values the list of values to count
     * @return a histogram mapping each value to its count
     */
    public static <K> Histogram<K> createHistogram(List<K> values) {
        Histogram<K> histogram = new Histogram<K>();

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

    /**
     * Creates a histogram of the specified column in a dataset.
     *
     * @param dataset the dataset to read
     * @param column  the column index to count
     * @return a histogram mapping each value to its count
     */
    public static Histogram<String> createHistogram(IPVDataset dataset, int column) {
        return createHistogram(dataset, column, false);
    }

    /**
     * Creates a histogram of the specified column in a dataset, optionally lower-casing values.
     *
     * @param dataset     the dataset to read
     * @param column      the column index to count
     * @param toLowercase whether to convert values to lower case before counting
     * @return a histogram mapping each value to its count
     */
    public static Histogram<String> createHistogram(IPVDataset dataset, int column, boolean toLowercase) {
        Histogram<String> histogram = new Histogram<>();

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
