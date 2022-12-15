/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmap;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.ola.Lattice;
import com.ibm.research.drl.dpt.anonymization.ola.LatticeNode;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.util.*;
import java.util.stream.Collectors;

public class KMap implements AnonymizationAlgorithm {
    private List<ColumnInformation> columnInformationList;
    private List<PrivacyConstraint> privacyConstraints;
    private IPVDataset originalDataset;
    private List<Partition> originalPartitions;
    private List<Partition> anonymizedPartitions;
    private double suppressionRate;
    private int suppressedRows;

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
        return this.originalPartitions;
    }

    @Override
    public List<Partition> getAnonymizedPartitions() {
        return this.anonymizedPartitions;
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities, Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes, List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.originalDataset = dataset;
        this.suppressionRate = ((KMapOptions) options).getSuppressionRate();

        AnonymizationUtils.initializeConstraints(dataset, columnInformationList, privacyConstraints);

        return this;
    }

    @Override
    public String getName() {
        return "K-map";
    }

    @Override
    public String getDescription() {
        return "K-map based anonymization";
    }

    private Partition anonymizePartition(Partition eqClass, List<LatticeNode> nodes, int suppressionBudget) {
        if (eqClass.size() == 0) {
            return new InMemoryPartition(Collections.emptyList());
        }

        boolean constraintMatch = AnonymizationUtils.checkPrivacyConstraints(this.privacyConstraints, eqClass, null);

        if (constraintMatch) {
            Partition cp = copyPartition(eqClass);
            cp.setAnonymous(true);
            return cp;
        }

        if (suppressionBudget >= eqClass.size()) {
            return null;
        }

        for (LatticeNode node : nodes) {
            int[] level = node.getValues();
            IPVDataset anonymized = DatasetGeneralizer.generalize(eqClass.getMember(), this.columnInformationList, level);
            Partition p = new InMemoryPartition(anonymized);

            if (AnonymizationUtils.checkPrivacyConstraints(this.privacyConstraints, p, null)) {
                p.setAnonymous(true);
                return p;
            }
        }

        throw new RuntimeException("unreachable");
    }

    private Partition copyPartition(Partition eqClass) {
        return eqClass;
    }

    private List<LatticeNode> calculateNodes() {

        List<ColumnInformation> quasiColumnInformationList = this.columnInformationList.stream().filter(columnInformation -> {
            return columnInformation.getColumnType() == ColumnType.QUASI;
        }).collect(Collectors.toList());

        int[] maxLevel = new int[quasiColumnInformationList.size()];

        for (int i = 0; i < quasiColumnInformationList.size(); i++) {
            CategoricalInformation categoricalInformation = (CategoricalInformation) quasiColumnInformationList.get(i);
            int maximumLevel = categoricalInformation.getMaximumLevel();
            maxLevel[i] = maximumLevel >= 0 ? maximumLevel : categoricalInformation.getHierarchy().getHeight();
        }

        int[][] levels = new int[quasiColumnInformationList.size()][];

        for (int i = 0; i < quasiColumnInformationList.size(); i++) {
            int maxCurrentLevel = maxLevel[i];
            levels[i] = new int[maxCurrentLevel];
            for (int k = 0; k < maxCurrentLevel; k++) {
                levels[i][k] = k;
            }
        }

        List<LatticeNode> nodes = new ArrayList<>();

        List<List<Integer>> productResult = Lattice.calculateProduct(levels);
        for (List<Integer> l : productResult) {
            nodes.add(new LatticeNode(l));
        }

        nodes.sort(Comparator.comparingInt(LatticeNode::sum));
        return nodes;

    }

    @Override
    public IPVDataset apply() {
        List<Integer> matchColumns = AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.QUASI);

        int suppressionBudget = (int) Math.ceil((this.suppressionRate / 100.0) * this.originalDataset.getNumberOfRows());
        this.suppressedRows = 0;

        final List<Partition> partitions = AnonymizationUtils.generatePartitionsByColumnIndex(this.originalDataset, matchColumns);

        this.anonymizedPartitions = new ArrayList<>(partitions.size());

        List<LatticeNode> possibleNodes = calculateNodes();

        this.originalPartitions = partitions;

        for (Partition partition : partitions) {
            Partition anonymizedPartition = anonymizePartition(partition, possibleNodes, suppressionBudget);

            if (anonymizedPartition == null) {
                suppressionBudget -= partition.size();
                this.suppressedRows += partition.size();
                Partition cp = copyPartition(partition);
                cp.setAnonymous(false);
                this.anonymizedPartitions.add(cp);
                continue;
            }

            this.anonymizedPartitions.add(anonymizedPartition);
        }

        return createDataset(this.anonymizedPartitions, this.originalDataset.getNumberOfColumns());
    }

    private IPVDataset createDataset(List<Partition> anonymizedPartitions, int numberOfColumns) {
        IPVDataset dataset = new IPVDataset(numberOfColumns);

        for (Partition partition : anonymizedPartitions) {
            if (!partition.isAnonymous()) {
                continue;
            }

            IPVDataset members = partition.getMember();

            for (int i = 0; i < members.getNumberOfRows(); i++) {
                dataset.addRow(members.getRow(i));
            }
        }

        return dataset;
    }


    @Override
    public double reportSuppressionRate() {
        return 100.0 * (double) this.suppressedRows / (double) this.originalDataset.getNumberOfRows();
    }
}
