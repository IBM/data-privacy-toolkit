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

    @Override
    public String getName() {
        return "K-Means";
    }

    @Override
    public String getDescription() {
        return "K-Means based anonymization";
    }

    @Override
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

    @Override
    public double reportSuppressionRate() {
        return 100.0 * (double) this.strategyImpl.getSuppressedRows() / (double) original.getNumberOfRows();
    }
}

