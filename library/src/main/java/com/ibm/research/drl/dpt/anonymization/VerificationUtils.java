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

public class VerificationUtils {

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
