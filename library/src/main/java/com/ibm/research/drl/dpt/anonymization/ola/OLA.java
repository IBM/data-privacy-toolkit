/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.TypeClass;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import com.ibm.research.drl.schema.IPVSchema;
import com.ibm.research.drl.schema.IPVSchemaField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OLA implements AnonymizationAlgorithm {
    private IPVDataset original;
    private double suppressionRate;
    private List<ColumnInformation> columnInformationList;
    
    private List<Partition> originalPartitions;
    private List<Partition> anonymizedPartitions;
    
    private Lattice lattice;
    private LatticeNode bestNode;
    private List<PrivacyConstraint> privacyConstraints;
    private List<Integer> sensitiveColumns;
    
    @Override
    public TransformationType getTransformationType() {
        return TransformationType.GLOBAL_RECODING;
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
    public AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                             Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes,
                                             List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options) {
        return initialize(dataset, buildColumnInformationList(dataset, vulnerabilities, sensitiveFields, fieldTypes), privacyConstraints, options);
    }

    @Override
    public AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList, List<PrivacyConstraint> privacyConstraints,
                                             AnonymizationAlgorithmOptions options) {
        if (!(options instanceof OLAOptions)) throw new IllegalArgumentException("Expecting instance of OLAOptions");
        if (columnInformationList.size() != dataset.getNumberOfColumns()) throw new IllegalArgumentException("Number of column information not matching number of columns of the dataset");

        this.original = dataset;
        this.suppressionRate = ((OLAOptions) options).getSuppressionRate();
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;

        AnonymizationUtils.initializeConstraints(dataset, columnInformationList, privacyConstraints);
        
        return this;
    }

    @Override
    public String getName() {
        return "OLA";
    }

    @Override
    public String getDescription() {
        return "Optimal Lattice Anonymization";
    }

    private boolean checkConstraints(Partition partition) {
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (!privacyConstraint.check(partition, sensitiveColumns)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate partitions list.
     *
     * @param anonymized the anonymized
     * @return the list
     */

    // bestLevel is a collection with the generalization levels for the quasi-identifiers
    private IPVDataset createAnonymizedDataset(IPVDataset anonymized) {

        IPVDataset finalDataset = new IPVDataset(new ArrayList<>(), anonymized.getSchema(), anonymized.hasColumnNames());

        int numPartitions = this.originalPartitions.size();
        
        for(int n = 0; n < numPartitions; n++) {
            Partition p = this.originalPartitions.get(n);
            Partition anonP = this.anonymizedPartitions.get(n);
                    
            if (!checkConstraints(p)) {
                p.setAnonymous(false);
                anonP.setAnonymous(false);
                continue;
            }

            IPVDataset member = anonP.getMember();
            int memberRows = member.getNumberOfRows();

            for (int i = 0; i < memberRows; i++) {
                finalDataset.addRow(member.getRow(i));
            }
            
            p.setAnonymous(true);
            anonP.setAnonymous(true);
        }

        return finalDataset;
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
        return this.originalPartitions;
    }

    public List<Partition> getAnonymizedPartitions() {
        return this.anonymizedPartitions;
    }
    /**
     * Gets total nodes.
     *
     * @return the total nodes
     */
    public int getTotalNodes() {
        return lattice.getTotalNodes();
    }

    /**
     * Gets nodes checked.
     *
     * @return the nodes checked
     */
    public int getNodesChecked() {
        return lattice.getNodesChecked();
    }

    

    public static LatticeNode selectLowestLossOnLevel(List<LatticeNode> nodes) {

        double lowestLoss = Double.MAX_VALUE;
        LatticeNode matchNode = null;

        for (LatticeNode n : nodes) {
            Double informationLoss = n.getInformationLoss();
            if (informationLoss == null) { //these came from predictive tagging
                continue;
            }

            if (informationLoss < lowestLoss) {
                lowestLoss = informationLoss;
                matchNode = n;
            }
        }

        return matchNode;
    }

    @Override
    public IPVDataset apply() {
        if (null == original
            || null == columnInformationList
            || null == privacyConstraints) throw new RuntimeException("Not initialized yet");

        if (columnInformationList.stream().noneMatch(columnInformation -> columnInformation.getColumnType().equals(ColumnType.QUASI))) return original;

        AnonymityChecker anonymityChecker = new SimpleAnonymityChecker(original, this.columnInformationList, this.privacyConstraints);
        lattice = new Lattice(anonymityChecker, this.columnInformationList, this.suppressionRate);
        lattice.explore();
        

        List<LatticeNode> kMinimalNodes = lattice.getKMinimal();

        if (!kMinimalNodes.isEmpty()) {
            bestNode = selectLowestLossOnLevel(kMinimalNodes);
        } else {
            throw new RuntimeException("unable to find a suitable generalization");
        }

        IPVDataset anonymized = DatasetGeneralizer.generalize(original, this.columnInformationList, bestNode.getValues());
        Tuple<List<Partition>, List<Partition>> bothPartitions = 
                OLAUtils.generatePartitions(original, anonymized, this.columnInformationList);
        
        this.originalPartitions = bothPartitions.getFirst();
        this.anonymizedPartitions = bothPartitions.getSecond();

        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.SENSITIVE);

        //we need to suppress the non-k-anonymous values at this stage
        return createAnonymizedDataset(anonymized);
    }

    @Override
    public double reportSuppressionRate() {
        return bestNode.getSuppressionRate();
    }

    public LatticeNode reportBestNode() {
        return bestNode;
    }

    public LatticeNode reportMaxNode() { return lattice.getMaxNode(); }

    public List<LatticeNode> reportLossOnAllNodes() {
        return lattice.reportLossOnAllNodes();
    }
    
    public int getTagsPerformed() {
        return lattice.getTagsPerformed();
    }
}
