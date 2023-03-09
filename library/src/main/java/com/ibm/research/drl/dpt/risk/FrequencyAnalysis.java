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
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.*;

public class FrequencyAnalysis {
    private final Map<String, Integer> auxiliaryDataRanked;
    private final Map<String, String> maskedToOriginal;

    public FrequencyAnalysis(Map<String, String> originalToMasked, List<String> auxiliaryDataRankedList) {
        this.auxiliaryDataRanked = new HashMap<>();
        for (int i = 0; i < auxiliaryDataRankedList.size(); i++) {
            this.auxiliaryDataRanked.put(auxiliaryDataRankedList.get(i).toLowerCase(), i);
        }

        this.maskedToOriginal = new HashMap<>();
        for (Map.Entry<String, String> entry : originalToMasked.entrySet()) {
            this.maskedToOriginal.put(entry.getValue().toLowerCase(), entry.getKey().toLowerCase());
        }

    }

    public long successfulMatches(IPVDataset maskedDataset, int columnIndex) {
        Histogram histogram = Histogram.createHistogram(maskedDataset, columnIndex, true);
        return successfulMatches(histogram);
    }

    private long successfulMatches(Histogram histogram) {
        long matches = 0;

        List<Map.Entry<String, Long>> list = new LinkedList<>(histogram.entrySet());
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));


        for (int index = 0; index < list.size(); index++) {
            Map.Entry<String, Long> entry = list.get(index);
            String maskedValue = entry.getKey().toLowerCase();
            String originalValue = maskedToOriginal.get(maskedValue);

            Integer auxiliaryRank = auxiliaryDataRanked.get(originalValue);
            if (auxiliaryRank == null) {
                continue;
            }

            if (auxiliaryRank == index) {
                matches += entry.getValue();
            }
        }

        return matches;
    }

}
