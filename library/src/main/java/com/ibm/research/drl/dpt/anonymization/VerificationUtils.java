/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

        for(Set<Integer> value: valueMap.values()) {
            if (value.size() < k) {
                return true;
            }
        }

        return false;
    }

}
