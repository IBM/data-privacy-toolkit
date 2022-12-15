/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.TypeClass;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;

import java.util.*;

public class Mondrian implements AnonymizationAlgorithm {
    private IPVDataset dataset;
    private List<Integer> quasiColumns;
    private List<Integer> nonQuasiColumns;
    private final List<Partition> partitions = new ArrayList<>();
    private List<Partition> anonymizedPartitions = new ArrayList<>();
    private List<PrivacyConstraint> privacyConstraints;
    private List<ColumnInformation> columnInformationList;
    private CategoricalSplitStrategy categoricalSplitStrategy;

    public static List<Integer> getNonQuasiColumns(int numberOfColumns, List<Integer> quasiColumns) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < numberOfColumns; i++) {
            if (quasiColumns.contains(i)) {
                continue;
            }

            result.add(i);
        }

        return result;
    }

    @Override
    public TransformationType getTransformationType() {
        return TransformationType.LOCAL_RECODING;
    }

    @Override
    public List<ColumnInformation> getColumnInformationList() {
        return this.columnInformationList;
    }

    /**
     * Gets partitions.
     *
     * @return the partitions
     */
    public List<Partition> getOriginalPartitions() {
        return this.partitions;
    }

    public List<Partition> getAnonymizedPartitions() {
        return this.anonymizedPartitions;
    }

    public static String findCommonAncestor(List<List<String>> values, int columnIndex, MaterializedHierarchy materializedHierarchy) {
        Set<String> uniqueValues = new HashSet<>();

        for (List<String> row : values) {
            String v = row.get(columnIndex);
            uniqueValues.add(v);
        }

        return ClusteringAnonUtils.calculateCommonAncestor(uniqueValues, materializedHierarchy);
    }

    private static List<String> calculateCommonAncestors(MondrianPartition partition, List<ColumnInformation> columnInformationList) {

        List<String> commonAncestors = new ArrayList<>();

        for (int columnIndex = 0; columnIndex < columnInformationList.size(); columnIndex++) {
            ColumnInformation columnInformation = columnInformationList.get(columnIndex);
            if (columnInformation.getColumnType() != ColumnType.QUASI || !columnInformation.isCategorical()) {
                commonAncestors.add(null);
                continue;
            }

            CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
            String commonAncestor = findCommonAncestor(partition.getMember().getValues(), columnIndex,
                    (MaterializedHierarchy) categoricalInformation.getHierarchy());
            commonAncestors.add(commonAncestor);
        }

        return commonAncestors;
    }

    public static Partition anonymizePartition(MondrianPartition partition,
                                               List<Integer> quasiColumns, List<Integer> nonQuasiColumns,
                                               List<ColumnInformation> columnInformationList, CategoricalSplitStrategy categoricalSplitStrategy) {
        List<List<String>> anonymizedValues = new ArrayList<>();

        IPVDataset member = partition.getMember();
        List<String> middle = partition.getMiddle();

        List<String> commonAncestors = null;

        if (categoricalSplitStrategy == CategoricalSplitStrategy.ORDER_BASED) {
            commonAncestors = calculateCommonAncestors(partition, columnInformationList);
        }

        for (int i = 0; i < partition.size(); i++) {
            List<String> row = member.getRow(i);
            List<String> anonymizedRow = new ArrayList<>(row.size());

            for (int k = 0; k < row.size(); k++) {
                anonymizedRow.add(null);
            }

            for (int quasiCol : quasiColumns) {
                ColumnInformation columnInformation = columnInformationList.get(quasiCol);
                if (!columnInformation.isCategorical() || categoricalSplitStrategy == CategoricalSplitStrategy.HIERARCHY_BASED) {
                    anonymizedRow.set(quasiCol, middle.get(quasiCol));
                } else {
                    assert commonAncestors != null;
                    anonymizedRow.set(quasiCol, commonAncestors.get(quasiCol));
                }
            }

            for (Integer k : nonQuasiColumns) {
                anonymizedRow.set(k, row.get(k));
            }

            anonymizedValues.add(anonymizedRow);
        }

        return new InMemoryPartition(anonymizedValues);
    }


    private IPVDataset getAnonymizedDataset() {
        List<List<String>> values = new ArrayList<>();

        for (Partition p : partitions) {
            MondrianPartition partition = (MondrianPartition) p;

            Partition anonP = anonymizePartition(partition, this.quasiColumns, this.nonQuasiColumns,
                    this.columnInformationList, this.categoricalSplitStrategy);
            values.addAll(anonP.getMember().getValues());
            this.anonymizedPartitions.add(anonP);

            p.setAnonymous(true);
            anonP.setAnonymous(true);
        }

        return new IPVDataset(values, dataset.getSchema(), dataset.hasColumnNames());
    }

    private void anonymize(MondrianPartition partition, int level) {

        if (!partition.isSplittable()) {
            partitions.add(partition);
            return;
        }

        int dim = partition.chooseDimension();

        List<MondrianPartition> subPartitions = partition.split(dim, level);

        if (subPartitions.size() == 0) {
            partition.disallow(dim);
            anonymize(partition, level + 1);
        } else {
            for (MondrianPartition p : subPartitions) {
                anonymize(p, level + 1);
            }
        }

    }

    private double calculateCardinality(IPVDataset dataset, int columnIndex) {
        int numberOfRows = dataset.getNumberOfRows();
        Set<String> values = new HashSet<>();

        for (int i = 0; i < numberOfRows; i++) {
            String value = dataset.get(i, columnIndex);
            values.add(value.toLowerCase());
        }

        return values.size();
    }

    @Override
    public IPVDataset apply() {
        if (dataset == null || dataset.getNumberOfRows() == 0) {
            return dataset;
        }

        List<String> middle = new ArrayList<>();
        List<Interval> width = new ArrayList<>();

        for (int i = 0; i < dataset.getNumberOfColumns(); i++) {
            ColumnInformation columnInformation = columnInformationList.get(i);

            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                if (!columnInformation.isCategorical()) {
                    NumericalRange numericalInformation = (NumericalRange) columnInformation;
                    middle.add(MondrianPartition.generateMiddleKey(numericalInformation.getLow(), numericalInformation.getHigh()));
                    width.add(new Interval(numericalInformation.getLow(), numericalInformation.getHigh()));
                } else {
                    CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
                    String topTerm = categoricalInformation.getHierarchy().getTopTerm();
                    middle.add(topTerm.toUpperCase());
                    width.add(new Interval(0d, calculateCardinality(dataset, i)));
                }
            } else {
                middle.add(null);
                width.add(null);
            }
        }

        anonymize(new MondrianPartition(dataset, middle, width, columnInformationList, this.privacyConstraints, this.categoricalSplitStrategy), 0);

        return getAnonymizedDataset();
    }

    @Override
    public double reportSuppressionRate() {
        return 0.0;
    }

    private void initialize() {
    }

    private List<ColumnInformation> buildColumnInformationList(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                                               Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes) {
        List<ColumnInformation> columnInformationList = new ArrayList<>(dataset.getNumberOfColumns());

        IPVSchema schema = dataset.getSchema();
        List<? extends IPVSchemaField> fields = schema.getFields();

        IPVVulnerability vulnerability = AnonymizationUtils.mergeVulnerabilities(vulnerabilities);

        final boolean isForLinking = false;

        for (int i = 0; i < dataset.getNumberOfColumns(); i++) {
            String fieldName = fields.get(i).getName();

            if (sensitiveFields.contains(fieldName)) {
                columnInformationList.add(new SensitiveColumnInformation(isForLinking));
                continue;
            }

            boolean isQuasi = vulnerability.contains(i);
            if (!isQuasi) {
                columnInformationList.add(new DefaultColumnInformation(isForLinking));
                continue;
            }

            ProviderType fieldType = fieldTypes.get(fieldName);
            if (fieldType == null) {
                columnInformationList.add(ColumnInformationGenerator.generateCategoricalFromData(dataset, i, ColumnType.QUASI));
                continue;
            }

            if (fieldType.getTypeClass() == TypeClass.NUMERICAL) {
                NumericalRange numericalRange = ColumnInformationGenerator.generateNumericalRange(dataset, i, ColumnType.QUASI);
                columnInformationList.add(numericalRange);
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
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                             Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        return initialize(dataset, buildColumnInformationList(dataset, vulnerabilities, sensitiveFields, fieldTypes), privacyConstraints, options);
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {

        if (columnInformationList.size() != dataset.getNumberOfColumns())
            throw new IllegalArgumentException("Number of column information not matching number of columns of the dataset");

        this.categoricalSplitStrategy = CategoricalSplitStrategy.ORDER_BASED;

        if (options != null) {
            if (!(options instanceof MondrianOptions)) {
                throw new IllegalArgumentException("Expecting instance of OLAOptions");
            }

            MondrianOptions mondrianOptions = (MondrianOptions) options;
            this.categoricalSplitStrategy = mondrianOptions.getCategoricalSplitStrategy();
        }

        this.dataset = dataset;
        this.quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        this.nonQuasiColumns = getNonQuasiColumns(dataset.getNumberOfColumns(), this.quasiColumns);
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.anonymizedPartitions = new ArrayList<>();

        AnonymizationUtils.initializeConstraints(dataset, columnInformationList, privacyConstraints);

        initialize();

        return this;

    }

    @Override
    public String getName() {
        return "Mondrian";
    }

    @Override
    public String getDescription() {
        return "Implementation of Mondrian algorithm for K-anonymity with L-diversity support";
    }
}

