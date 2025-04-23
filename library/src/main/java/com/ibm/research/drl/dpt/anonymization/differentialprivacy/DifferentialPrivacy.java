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
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.ola.IntervalGenerator;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.TypeClass;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DifferentialPrivacy implements AnonymizationAlgorithm {
    private boolean perEquivalenceClass = true;
    public int columnIndex;
    private int numberOfColumns;
    private boolean getBoundsFromData;
    private DPMechanism mechanism;
    private IPVDataset dataset;
    private List<ColumnInformation> columnInformationList;
    private List<Partition> anonymizedPartitions;

    @Override
    public TransformationType getTransformationType() {
        return null;
    }

    @Override
    public List<ColumnInformation> getColumnInformationList() {
        return null;
    }

    private List<ColumnInformation> buildColumnInformationList(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                                               Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes) {
        List<ColumnInformation> columnInformationList = new ArrayList<>(dataset.getNumberOfColumns());

        IPVSchema schema = dataset.getSchema();
        List<? extends IPVSchemaField> fields = schema.getFields();

        IPVVulnerability vulnerability = AnonymizationUtils.mergeVulnerabilities(vulnerabilities);

        for (int i = 0; i < dataset.getNumberOfColumns(); i++) {
            String fieldName = fields.get(i).getName();

            if (sensitiveFields.contains(fieldName)) {
                columnInformationList.add(new SensitiveColumnInformation());
                continue;
            }

            boolean isQuasi = vulnerability.contains(i);
            if (!isQuasi) {
                columnInformationList.add(new DefaultColumnInformation());
                continue;
            }

            ProviderType fieldType = fieldTypes.get(fieldName);
            if (fieldType == null) {
                columnInformationList.add(ColumnInformationGenerator.generateCategoricalFromData(dataset, i, ColumnType.QUASI));
                continue;
            }

            if (fieldType.getTypeClass() == TypeClass.NUMERICAL) {
                GeneralizationHierarchy hierarchy = IntervalGenerator.generateHierarchy(dataset, i);
                columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));
            } else {
                GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(fieldType);
                if (hierarchy == null) {
                    columnInformationList.add(ColumnInformationGenerator.generateCategoricalFromData(dataset, i, ColumnType.QUASI));
                } else {
                    columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));
                }
            }
        }

        return columnInformationList;
    }

    @Override
    public List<Partition> getOriginalPartitions() {
        if (this.perEquivalenceClass) {
            return AnonymizationUtils.generatePartitions(dataset, columnInformationList);
        } else {
            return Collections.singletonList(new InMemoryPartition(dataset));
        }
    }

    @Override
    public List<Partition> getAnonymizedPartitions() {
        return this.anonymizedPartitions;
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                             Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        return initialize(dataset, buildColumnInformationList(dataset, vulnerabilities, sensitiveFields, fieldTypes), privacyConstraints, options);
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList, List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        if (!(options instanceof DifferentialPrivacyMechanismOptions)) throw new IllegalArgumentException("Expecting instance of DifferentialPrivacyMechanismOptions");

        this.dataset = dataset;
        this.columnInformationList = columnInformationList;
        this.mechanism = ((DifferentialPrivacyMechanismOptions) options).getMechanism();
        this.mechanism.setOptions(options);
        this.getBoundsFromData = ((DifferentialPrivacyMechanismOptions) options).isGetBoundsFromData();

        return this;
    }

    @Override
    public String getName() {
        return "Differential Privacy";
    }

    @Override
    public String getDescription() {
        return "Implements e-differential privacy on an e-quasi column of the dataset";
    }


    @Override
    public IPVDataset apply() {
        List<Integer> columnIndexes = IntStream.range(0, columnInformationList.size()).boxed().filter(i -> columnInformationList.get(i).getColumnType().equals(ColumnType.E_QUASI)).collect(Collectors.toList());
        if (columnIndexes.size() != 1) throw new RuntimeException("More than one e-quasi identifier specified");

        this.columnIndex = columnIndexes.get(0);

        if (perEquivalenceClass) {
            this.anonymizedPartitions = new ArrayList<>();
            for (Partition equivalenceClass : AnonymizationUtils.generatePartitions(dataset, columnInformationList)) {
                if (this.getBoundsFromData) {
                    this.mechanism.analyseForParams(Collections.singletonList(equivalenceClass), columnIndex);
                }

                Partition anonymizedPartition = applyDifferentialPrivacy(equivalenceClass.getMember(), this.columnIndex);
                this.anonymizedPartitions.add(anonymizedPartition);
            }

            return consolidatePartitions(this.anonymizedPartitions);
        } else {
            this.anonymizedPartitions = new ArrayList<>();
            if (this.getBoundsFromData) {
                this.mechanism.analyseForParams(Collections.singletonList(new InMemoryPartition(dataset)), this.columnIndex);
            }

            Partition anonymizedPartition = applyDifferentialPrivacy(dataset, this.columnIndex);
            this.anonymizedPartitions.add(anonymizedPartition);
            return anonymizedPartition.getMember();
        }
    }

    private Partition applyDifferentialPrivacy(IPVDataset dataset, int columnIndex) {
        List<List<String>> noisyDataset = new ArrayList<>();

        for (List<String> row : dataset) {
            List<String> anonymisedRow = new ArrayList<>(row);

            String value = row.get(columnIndex);
            String noisyValue = this.mechanism.randomise(value);

            anonymisedRow.set(columnIndex, noisyValue);
            noisyDataset.add(anonymisedRow);
        }

        Partition finalDataset = new InMemoryPartition(noisyDataset);
        finalDataset.setAnonymous(true);

        return finalDataset;
    }

    private IPVDataset consolidatePartitions(List<Partition> equivalenceClasses) {
        IPVDataset finalDataset = new IPVDataset(new ArrayList<>(), IPVDataset.generateSchemaWithoutColumnNames(numberOfColumns), false);

        for (Partition p : equivalenceClasses) {
            IPVDataset member = p.getMember();
            int memberRows = member.getNumberOfRows();

            for (int i = 0; i < memberRows; i++) {
                finalDataset.addRow(member.getRow(i));
            }
        }

        return finalDataset;
    }

    @Override
    public double reportSuppressionRate() {
        return 0.0;
    }
}

