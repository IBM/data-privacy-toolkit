/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KMeansAnonymization implements AnonymizationAlgorithm {
    private int numberOfClusters;
    private List<ColumnInformation> columnInformationList;
    private List<PrivacyConstraint> privacyConstraints;
    private double suppressionRate;
    private IPVDataset original;
    private int k;
    private StrategyOptions strategy;

    private StrategyImpl strategyImpl;

    @Override
    public TransformationType getTransformationType() {
        return TransformationType.LOCAL_RECODING;
    }

    @Override
    public List<ColumnInformation> getColumnInformationList() {
        return this.columnInformationList;
    }

    @Override
    public List<Partition> getOriginalPartitions() {
        return this.strategyImpl.getOriginalPartitions();
    }

    @Override
    public List<Partition> getAnonymizedPartitions() {
        return this.strategyImpl.getAnonymizedPartitions();
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities, Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes, List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        return null;
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {

        KMeansOptions kMeansOptions = (KMeansOptions) options;

        this.k = AnonymizationUtils.getK(privacyConstraints);
        this.numberOfClusters = (int) Math.floor((double) dataset.getNumberOfRows() / (double) this.k);
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.original = dataset;
        this.suppressionRate = kMeansOptions.getSuppressionRate();
        this.strategy = kMeansOptions.getStrategy();

        AnonymizationUtils.initializeConstraints(dataset, columnInformationList, privacyConstraints);

        return this;
    }

    public String getName() {
        return "K-Means";
    }

    public String getDescription() {
        return "K-Means based anonymization";
    }

    public IPVDataset apply() {

        KMeans kMeans = new KMeans(this.original, this.numberOfClusters, 1000, this.columnInformationList,
                AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.QUASI));

        List<KMeansCluster> clusters = kMeans.apply();

        if (this.strategy == StrategyOptions.DUMMY) {
            this.strategyImpl = new DummySuppressionReassignment(original, suppressionRate, columnInformationList, privacyConstraints);
        } else {
            throw new RuntimeException("not implemented yet");
        }

        return this.strategyImpl.buildAnonymizedDataset(clusters);
    }

    public double reportSuppressionRate() {
        return 100.0 * (double) this.strategyImpl.getSuppressedRows() / (double) original.getNumberOfRows();
    }
}

