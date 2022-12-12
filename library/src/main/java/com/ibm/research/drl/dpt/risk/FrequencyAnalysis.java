/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.*;

public class FrequencyAnalysis {
    private final Map<String, Integer> auxiliaryDataRanked;
    private final Map<String, String> maskedToOriginal;
    
    public FrequencyAnalysis(Map<String, String> originalToMasked, List<String> auxiliaryDataRankedList) {
        this.auxiliaryDataRanked = new HashMap<>();
        for(int i = 0; i < auxiliaryDataRankedList.size(); i++) {
            this.auxiliaryDataRanked.put(auxiliaryDataRankedList.get(i).toLowerCase(), i);
        }
        
        this.maskedToOriginal = new HashMap<>();
        for(Map.Entry<String, String> entry: originalToMasked.entrySet()) {
            this.maskedToOriginal.put(entry.getValue().toLowerCase(), entry.getKey().toLowerCase());
        }
        
    }
    
    public int successfulMatches(IPVDataset maskedDataset, int columnIndex) {
        Histogram histogram = Histogram.createHistogram(maskedDataset, columnIndex, true);
        return successfulMatches(histogram);
    }
    
    private int successfulMatches(Histogram histogram) {
        int matches = 0;
        
        List<Map.Entry<String, Long>> list = new LinkedList<>(histogram.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        });
        
        
        for(int index = 0; index < list.size(); index++) {
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
