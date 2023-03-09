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
package com.ibm.research.drl.dpt.anonymization.kmeans;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.*;
import java.util.stream.Collectors;

public class DummySuppressionReassignment extends StrategyImpl {

    private final double maximumSuppressionRate;
    private final IPVDataset original;
    private final List<ColumnInformation> columnInformationList;
    private final List<PrivacyConstraint> privacyConstraints;
    private final List<Integer> sensitiveColumns;

    private List<List<Set<String>>> categoricalVariables;
    private Long suppressedRows = 0L;
    private final List<Partition> partitions = new ArrayList<>();
    private final List<Partition> anonymizedPartitions = new ArrayList<>();

    public DummySuppressionReassignment(IPVDataset original, double maximumSuppressionRate,
                                        List<ColumnInformation> columnInformationList,
                                        List<PrivacyConstraint> privacyConstraints) {

        super(original, maximumSuppressionRate, columnInformationList, privacyConstraints);
        this.original = original;
        this.maximumSuppressionRate = maximumSuppressionRate;
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.SENSITIVE);
    }

    public Long getSuppressedRows() {
        return suppressedRows;
    }

    public List<Partition> getOriginalPartitions() {
        return partitions;
    }

    public List<Partition> getAnonymizedPartitions() {
        return anonymizedPartitions;
    }


    private int checkNonConformingClusters(List<KMeansCluster> clusters) {
        int nonConforming = 0;

        for (KMeansCluster cluster : clusters) {
            Partition partition = cluster.getOriginalData();

            boolean conforms = AnonymizationUtils.checkPrivacyConstraints(privacyConstraints, partition, this.sensitiveColumns);
            partition.setAnonymous(conforms);

            if (!conforms) {
                nonConforming++;
            }
        }

        return nonConforming;
    }

    private void mergeClusters(KMeansCluster cluster, KMeansCluster targetCluster) {
        targetCluster.addValues(cluster.getValues());
        targetCluster.computeCenter();

        targetCluster.getOriginalData().getMember().append(cluster.getOriginalData().getMember().getValues());
    }

    private List<KMeansCluster> suppressAndMerge(List<KMeansCluster> clusters) {

        List<KMeansCluster> candidateClusters = new ArrayList<>(clusters);

        int nonConforming = checkNonConformingClusters(candidateClusters);

        while (nonConforming > 0) {
            List<Integer> toRemove = new ArrayList<>();

            for (int i = (candidateClusters.size() - 1); i >= 0; i--) {
                KMeansCluster cluster = candidateClusters.get(i);

                if (cluster.getOriginalData().isAnonymous()) {
                    continue;
                }

                int numberOfRecords = cluster.getOriginalData().size();

                double suppressionAfterRemoval = (100.0 * (numberOfRecords + suppressedRows)) / (double) this.original.getNumberOfRows();

                if (suppressionAfterRemoval <= this.maximumSuppressionRate) {
                    this.suppressedRows += numberOfRecords;
                } else {
                    int index = KMeans.getNearestPointIndex(cluster.getCenter(), candidateClusters, i);

                    if (index == Integer.MAX_VALUE) {
                        throw new RuntimeException("we could not merge with any cluster");
                    }

                    KMeansCluster targetCluster = candidateClusters.get(index);
                    mergeClusters(cluster, targetCluster);
                }

                toRemove.add(i);
            }

            for (Integer c : toRemove) {
                candidateClusters.remove(c.intValue());
            }

            nonConforming = checkNonConformingClusters(candidateClusters);
        }

        return candidateClusters;
    }


    private List<String> buildAnonymizedRow(List<String> originalRow, List<String> categoricalCentroids, List<Double> clusterCenter) {
        List<String> anonymizedRow = new ArrayList<>();

        int quasiIndex = 0;

        for (int i = 0; i < columnInformationList.size(); i++) {
            ColumnInformation columnInformation = this.columnInformationList.get(i);

            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                anonymizedRow.add(originalRow.get(i));
            } else {
                boolean isCategorical = columnInformation.isCategorical();

                if (isCategorical) {
                    anonymizedRow.add(categoricalCentroids.get(quasiIndex));
                } else {
                    anonymizedRow.add(clusterCenter.get(quasiIndex).toString());
                }

                quasiIndex++;
            }
        }

        return anonymizedRow;
    }

    private List<String> createCategoricalCentroids(Partition partition) {

        List<Integer> categoricalColumns = AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.QUASI);
        categoricalColumns = categoricalColumns.stream().filter(x -> this.columnInformationList.get(x).isCategorical()).collect(Collectors.toList());

        if (categoricalColumns.isEmpty()) {
            return Collections.emptyList();
        }

        List<Set<String>> values = new ArrayList<>();
        List<GeneralizationHierarchy> hierarchies = new ArrayList<>();

        for (Integer categoricalColumn : categoricalColumns) {
            values.add(new HashSet<>());
            CategoricalInformation categoricalInformation = (CategoricalInformation) this.columnInformationList.get(categoricalColumn);
            hierarchies.add(categoricalInformation.getHierarchy());
        }

        for (List<String> originalRow : partition.getMember()) {
            for (int i = 0; i < categoricalColumns.size(); i++) {
                Integer column = categoricalColumns.get(i);
                String value = originalRow.get(column);

                values.get(i).add(value);
            }
        }

        List<String> centroids = new ArrayList<>();

        for (int i = 0; i < categoricalColumns.size(); i++) {
            GeneralizationHierarchy hierarchy = hierarchies.get(i);
            centroids.add(ClusteringAnonUtils.calculateCommonAncestor(values.get(i), hierarchy));
        }

        return centroids;
    }

    private IPVDataset createAnonymized(List<KMeansCluster> clusters) {
        IPVDataset anonymized = new IPVDataset(original.getNumberOfColumns());

        for (KMeansCluster cluster : clusters) {
            Partition partition = cluster.getOriginalData();

            if (partition.size() == 0) {
                continue;
            }

            List<Double> clusterCenter = cluster.getCenter();

            List<String> categoricalCentroids = createCategoricalCentroids(partition);

            for (List<String> originalRow : partition.getMember()) {
                anonymized.addRow(buildAnonymizedRow(originalRow, categoricalCentroids, clusterCenter));
            }
        }

        return anonymized;
    }


    public IPVDataset buildAnonymizedDataset(List<KMeansCluster> clusters) {


        List<KMeansCluster> conformingClusters = suppressAndMerge(clusters);

        return createAnonymized(conformingClusters);

    }


}

