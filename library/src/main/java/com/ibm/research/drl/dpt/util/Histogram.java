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
