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
package com.ibm.research.drl.dpt.anonymization;


import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Utility methods for verifying quasi-identifier properties of datasets. */
public class VerificationUtils {

    /** Not instantiable. */
    private VerificationUtils() {}

    /**
     * Builds a map from combined quasi-identifier values to the set of row indices that share those values.
     *
     * @param itemSet the set of column indices forming the quasi-identifier
     * @param dataset the dataset to process
     * @return a map from combined value strings to sets of row indices
     */
    public static Map<String, Set<Integer>> buildValueMap(ItemSet itemSet, IPVDataset dataset) {
        Map<String, Set<Integer>> valueMap = new HashMap<>();

        // build valueMap
        for (int rowId = 0; rowId < dataset.getNumberOfRows(); ++rowId) {
            // build item
            StringBuilder builder = new StringBuilder();

            for (Integer item : itemSet.getItems()) {
                builder.append(',');
                builder.append(dataset.get(rowId, item));
            }

            String key = builder.toString();

            // add rowId
            Set<Integer> rowIdSet = valueMap.computeIfAbsent(key, k -> new HashSet<>());

            rowIdSet.add(rowId);
        }

        return valueMap;
    }

    /**
     * Determines whether an item set is a quasi-identifier with respect to k.
     *
     * @param itemSet the set of column indices to test
     * @param dataset the dataset to check
     * @param k       the minimum group size required for non-identification
     * @return {@code true} if some combination of values appears in fewer than {@code k} rows
     */
    public static boolean isQuasiIdentifier(ItemSet itemSet, IPVDataset dataset, int k) {
        Map<String, Set<Integer>> valueMap = buildValueMap(itemSet, dataset);

        for (Set<Integer> value : valueMap.values()) {
            if (value.size() < k) {
                return true;
            }
        }

        return false;
    }

}
