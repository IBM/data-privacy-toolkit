/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;

import com.ibm.research.drl.dpt.anonymization.InMemoryPartition;
import com.ibm.research.drl.dpt.anonymization.Partition;

import java.util.ArrayList;
import java.util.List;

public class KMeansCluster {
    
    private List<Double> center;
    private final List<List<Double>> values;
    private Partition originalData;
    
    public KMeansCluster() {
        this.center = null;
        this.originalData = null;
        this.values = new ArrayList<>();
    }
    
    public KMeansCluster(List<Double> center) {
        this.center = center;
        this.originalData = null;
        this.values = new ArrayList<>();
    }

    public List<List<Double>> getValues() {
        return values;
    }

    public void addValues(List<List<Double>> toAppend) {
        this.values.addAll(toAppend);
    }
    
    public void initializePartition(int numberOfColumns) {
        this.originalData = new InMemoryPartition(numberOfColumns);
        this.originalData.setAnonymous(false);
    }

    public void addOriginalRow(List<String> datasetRow) {
        this.originalData.getMember().addRow(datasetRow);
    }

    public Partition getOriginalData() {
        return this.originalData;
    }
    
    public List<Double> getCenter() {
        return this.center;
    }
    
    public void add(List<Double> row) {
        this.values.add(row);
    }
   
    public void computeCenter() {
        if (values.size() == 0) {
            return;
        }

        List<Double> newCenter = values.get(0);
        
        for(int i = 1; i < values.size(); i++) {
            List<Double> row = values.get(i);
            
            for(int j = 0; j < row.size(); j++) {
                newCenter.set(j, row.get(j) + newCenter.get(j));
            }
        }

        newCenter.replaceAll(aDouble -> aDouble / (double) values.size());
        
        this.center = newCenter;
    }

}

