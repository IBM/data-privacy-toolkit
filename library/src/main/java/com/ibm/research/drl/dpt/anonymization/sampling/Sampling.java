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
package com.ibm.research.drl.dpt.anonymization.sampling;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.security.SecureRandom;
import java.util.*;

public class Sampling implements AnonymizationAlgorithm {

    private IPVDataset original;
    private List<Partition> originalPartitions;
    private List<Partition> anonymizedPartitions;

    private List<ColumnInformation> columnInformationList;
    private double percentage;

    @Override
    public TransformationType getTransformationType() {
        return TransformationType.GLOBAL_RECODING;
    }

    @Override
    public List<ColumnInformation> getColumnInformationList() {
        return this.columnInformationList;
    }

    @Override
    public List<Partition> getOriginalPartitions() {
        return this.originalPartitions;
    }

    @Override
    public List<Partition> getAnonymizedPartitions() {
        return this.anonymizedPartitions;
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities, Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes, List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        return null;
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset,
                                             List<ColumnInformation> columnInformationList,
                                             List<PrivacyConstraint> privacyConstraints,
                                             AnonymizationAlgorithmOptions options) {
        this.original = dataset;
        this.columnInformationList = columnInformationList;
        this.percentage = ((SamplingOptions) options).getPercentage();

        if (percentage < 0.0 || percentage > 1.0) {
            throw new RuntimeException("invalid percentage value: " + percentage);
        }

        return this;
    }

    @Override
    public String getName() {
        return "Sampling";
    }

    @Override
    public String getDescription() {
        return "Sampling based anonymization";
    }

    @Override
    public IPVDataset apply() {
        int populationSize = this.original.getNumberOfRows();
        int sampleSize = (int) (this.percentage * populationSize);

        if (sampleSize == 0) {
            return new IPVDataset(
                    new ArrayList<>(),
                    original.getSchema(),
                    original.hasColumnNames()
            );
        }

        List<List<String>> finalRows = new ArrayList<>();
        Random random = new SecureRandom();

        Set<Integer> used = new HashSet<>();

        Partition originalAnonymous = new InMemoryPartition(this.original.getNumberOfColumns());
        Partition originalNonAnonymous = new InMemoryPartition(this.original.getNumberOfColumns());
        Partition anonymizedNonAnonymous = new InMemoryPartition(this.original.getNumberOfColumns());

        for (int i = 0; i < sampleSize; i++) {
            while (true) {
                int nextPos = random.nextInt(populationSize);
                if (used.contains(nextPos)) {
                    continue;
                }

                List<String> row = this.original.getRow(nextPos);

                finalRows.add(row);
                originalAnonymous.getMember().addRow(row);

                used.add(nextPos);
                break;
            }
        }

        for (int i = 0; i < this.original.getNumberOfRows(); i++) {
            if (!used.contains(i)) {
                List<String> row = this.original.getRow(i);
                originalNonAnonymous.getMember().addRow(row);
                anonymizedNonAnonymous.getMember().addRow(row);
            }
        }

        Partition anonymizedAnonymous = new InMemoryPartition(finalRows);

        originalAnonymous.setAnonymous(true);
        originalNonAnonymous.setAnonymous(false);
        this.originalPartitions = Arrays.asList(originalAnonymous, originalNonAnonymous);

        anonymizedAnonymous.setAnonymous(true);
        anonymizedNonAnonymous.setAnonymous(false);
        this.anonymizedPartitions = Arrays.asList(anonymizedAnonymous, anonymizedNonAnonymous);

        return new IPVDataset(
                finalRows,
                original.getSchema(),
                original.hasColumnNames()
        );
    }

    @Override
    public double reportSuppressionRate() {
        return 0;
    }
}

