/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;


import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.MutableTuple;

import java.util.ArrayList;
import java.util.List;

public class KMeans {
    
    private final int k;
    private final IPVDataset dataset;
    private final List<List<Double>> datasetNormalizedValues;
    private final int maxIterations; 
    private final List<ColumnInformation> columnInformation;
    private final List<Integer> columnsToCluster;

    private List<MutableTuple<Double,Double>> findMinMax() {
        List<MutableTuple<Double,Double>> minMaxValues = new ArrayList<>(this.columnsToCluster.size());
        
        for(Integer column: columnsToCluster) {
            minMaxValues.add(new MutableTuple<>(Double.MAX_VALUE, Double.MIN_VALUE));
        }
        
        for(List<String> row: this.dataset) {
            for(int i = 0; i < columnsToCluster.size(); i++) {
                Integer column = columnsToCluster.get(i);
                
                double value = getValue(column, row); 
                
                MutableTuple<Double, Double> minMaxForColumn = minMaxValues.get(i);
                minMaxForColumn.setFirst(Math.min(value, minMaxForColumn.getFirst()));
                minMaxForColumn.setSecond(Math.max(value, minMaxForColumn.getSecond()));
            }
        }
       
        return minMaxValues;
    }
    
    private List<KMeansCluster> initializeRandomCenters(int n) {
        List<KMeansCluster> centers = new ArrayList<>(n);
        
        List<MutableTuple<Double, Double>> minMaxValues = findMinMax();
        
        for (int i = 0; i < n; i++) {
            
            List<Double> meanPoint = new ArrayList<>();

            for(MutableTuple<Double, Double> minMax: minMaxValues) {
                double min = minMax.getFirst();
                double max = minMax.getSecond();
                
                double rnd = min + Math.random() * (max - min);
                meanPoint.add(rnd);
            }
            
            centers.add(new KMeansCluster(meanPoint));
        }
        
        return centers;
    }

    private static double euclideanDistance(List<Double> row, List<Double> center) {
        double distance = 0.0;
        
        for(int i = 0; i < row.size(); i++) {
            distance += Math.pow(row.get(i) - center.get(i), 2);
        }
        
        return Math.sqrt(distance);
    }
    
    private int getNearestPointIndex(List<Double> row, List<KMeansCluster> clusters) {
        return getNearestPointIndex(row, clusters, -1);
    }
    
    static int getNearestPointIndex(List<Double> row, List<KMeansCluster> clusters, int excludeIndex) {
        double minDistance = Double.MAX_VALUE;
        int minIndex = Integer.MAX_VALUE;

        for(int i = 0; i < clusters.size(); i++) {
            KMeansCluster cluster = clusters.get(i);
            List<Double> center = cluster.getCenter();
           
            if (center == null) {
                continue;
            }
            
            double distance = euclideanDistance(row, center);
            
            if (distance < minDistance && i != excludeIndex) {
                minDistance = distance;
                minIndex = i;
            }
        }
        
        return minIndex;
    }


    private List<KMeansCluster> getNewCenters(List<KMeansCluster> centers) {
        
        List<KMeansCluster> clusters = new ArrayList<>(this.k);
        for (int i = 0; i < this.k; i++) {
            clusters.add(new KMeansCluster());
        }
        
        for (List<Double> row : this.datasetNormalizedValues) {
            int index = getNearestPointIndex(row, centers);
            clusters.get(index).add(row);
        }
        
        for (KMeansCluster cluster : clusters) {
            cluster.computeCenter();
        }
        
        return clusters;
    }
    
    private  boolean haveClustersConverged(List<KMeansCluster> oldCenters, List<KMeansCluster> newCenters) {
        double accumDist = 0;
        
        for (int i = 0; i < oldCenters.size(); i++) {
            List<Double> oldCenter = oldCenters.get(i).getCenter();
            List<Double> newCenter = newCenters.get(i).getCenter();
            
            if (oldCenter == null && newCenter == null) {
                continue;
            }
            
            if (oldCenter == null || newCenter == null) {
                return false;
            }
            
            double dist = euclideanDistance(oldCenter, newCenter); 
            accumDist += dist;
        }
        
        return accumDist == 0;
    }

    private List<KMeansCluster> applyAlgorithm(List<KMeansCluster> centers) {
        
        boolean converged;
        int iterations = 0;
        
        do {
            List<KMeansCluster> newCenters = getNewCenters(centers);
            converged = haveClustersConverged(centers, newCenters);
            centers = newCenters;
            iterations++;
        } while (!converged && iterations < maxIterations);
        
        return centers;
    }
    
   private double getValue(Integer column, List<String> row) {
       ColumnInformation columnInfo = this.columnInformation.get(column);
       String value = row.get(column);

       if (columnInfo.isCategorical()) {
           MaterializedHierarchy hierarchy = (MaterializedHierarchy) ((CategoricalInformation) columnInfo).getHierarchy();
           Integer index = hierarchy.getIndex(value);
           return (double) index;
       }
       else {
           return Double.parseDouble(value);
       }
   } 

    private List<Double> extractValues(List<String> row) { //XXX infinite replication
        List<Double> values = new ArrayList<>();

        for(Integer column: columnsToCluster) {
            values.add(getValue(column, row));
        }

        return values;
    }
    
    private List<List<Double>> normalizeDataset(IPVDataset dataset) {
        List<List<Double>> values = new ArrayList<>();
        
        for(List<String> row: dataset) {
            values.add(extractValues(row));
        }
        
        return values;
    }
    
    public List<KMeansCluster> apply() {
        List<KMeansCluster> centers = initializeRandomCenters(k); 
        List<KMeansCluster> clusters = applyAlgorithm(centers);
        
        return assignToPartitions(clusters);
        
    }

    private List<KMeansCluster> assignToPartitions(List<KMeansCluster> clusters) {
        int numOfRows = this.datasetNormalizedValues.size();

        for (KMeansCluster cluster : clusters) {
            cluster.initializePartition(this.dataset.getNumberOfColumns());
        }
        
        for(int i = 0; i < numOfRows; i++) {
            List<Double> normalized = this.datasetNormalizedValues.get(i);
            int index = getNearestPointIndex(normalized, clusters);
           
            List<String> datasetRow = this.dataset.getRow(i);
            clusters.get(index).addOriginalRow(datasetRow);
        }
        
        return clusters;
    }

    public KMeans(IPVDataset dataset, int k, int maxIterations, List<ColumnInformation> columnInformation,
                  List<Integer> columnsToCluster) {
        this.dataset = dataset;
        this.k = k;
        this.maxIterations = maxIterations;
        this.columnInformation = columnInformation;
        this.columnsToCluster = columnsToCluster;
        this.datasetNormalizedValues = normalizeDataset(dataset);
    }
}

