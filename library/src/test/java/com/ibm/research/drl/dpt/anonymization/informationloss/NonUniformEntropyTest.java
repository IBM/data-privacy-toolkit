/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchema;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchemaField;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NonUniformEntropyTest {

    @Test
    public void testNonUniformEntropy() {
        IPVDataset original = new IPVDataset(1);
        original.addRow(List.of("1"));
        original.addRow(List.of("2"));
        original.addRow(List.of("3"));
        original.addRow(List.of("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("3-4"));
        anonymized.addRow(List.of("3-4"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);
        
        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                List.of(originalPartition), List.of(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));
        
        double ne = nonUniformEntropy.report();

        assertEquals(4.0, ne, 0.01);
        assertEquals(upperBound, nonUniformEntropy.getUpperBound(), 0.01);
    }

    @Test
    public void testNonUniformEntropyWithWeights() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(List.of("1"));
        original.addRow(List.of("2"));
        original.addRow(List.of("3"));
        original.addRow(List.of("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("3-4"));
        anonymized.addRow(List.of("3-4"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI, 0.5));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                List.of(originalPartition), List.of(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));

        double ne = nonUniformEntropy.report();

        assertEquals(4.0 * 0.5, ne, 0.01);
        assertEquals(upperBound, nonUniformEntropy.getUpperBound(), 0.01);
    }

    @Test
    public void testNonUniformEntropyWithZeroWeights() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(List.of("1"));
        original.addRow(List.of("2"));
        original.addRow(List.of("3"));
        original.addRow(List.of("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("3-4"));
        anonymized.addRow(List.of("3-4"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI, 0));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                List.of(originalPartition), List.of(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));

        double ne = nonUniformEntropy.report();

        assertEquals(0.0, ne, 0.01);
        assertEquals(upperBound, nonUniformEntropy.getUpperBound(), 0.01);
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    @Test
    @Disabled
    public void logTests() {
        System.out.println(log2(0.2));
    }
    
    @Test
    public void testNonUniformEntropyWithSuppressed() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(List.of("1"));
        original.addRow(List.of("2"));
        original.addRow(List.of("3"));
        original.addRow(List.of("4"));
        original.addRow(List.of("5"));

        Partition originalPartition1 = new InMemoryPartition(1);
        originalPartition1.getMember().addRow(List.of("1"));
        originalPartition1.getMember().addRow(List.of("2"));
        originalPartition1.getMember().addRow(List.of("3"));
        originalPartition1.getMember().addRow(List.of("4"));
        originalPartition1.setAnonymous(true);

        Partition originalPartition2 = new InMemoryPartition(1);
        originalPartition2.getMember().addRow(List.of("5"));
        originalPartition2.setAnonymous(false);
       
        List<Partition> originalPartitions = new ArrayList<>();
        originalPartitions.add(originalPartition1);
        originalPartitions.add(originalPartition2);
        
        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("1-2"));
        anonymized.addRow(List.of("3-4"));
        anonymized.addRow(List.of("3-4"));
        anonymized.addRow(List.of("5-6"));

        Partition anonymizedPartition1 = new InMemoryPartition(1);
        anonymizedPartition1.getMember().addRow(List.of("1-2"));
        anonymizedPartition1.getMember().addRow(List.of("1-2"));
        anonymizedPartition1.getMember().addRow(List.of("3-4"));
        anonymizedPartition1.getMember().addRow(List.of("3-4"));
        anonymizedPartition1.setAnonymous(true);

        Partition anonymizedPartition2 = new InMemoryPartition(1);
        anonymizedPartition2.getMember().addRow(List.of("5-6"));
        anonymizedPartition2.setAnonymous(false);

        List<Partition> anonymizedPartitions = new ArrayList<>();
        anonymizedPartitions.add(anonymizedPartition1);
        anonymizedPartitions.add(anonymizedPartition2);

        MaterializedHierarchy materializedHierarchy = new MaterializedHierarchy();
        materializedHierarchy.add("1", "1-2", "*");
        materializedHierarchy.add("2", "1-2", "*");
        materializedHierarchy.add("3", "3-4", "*");
        materializedHierarchy.add("4", "3-4", "*");
        materializedHierarchy.add("5", "5-6", "*");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(materializedHierarchy, ColumnType.QUASI));

        InformationMetric nonUniformEntropy = 
                new NonUniformEntropy().initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, null);
        
        double expected = (-log2(0.2 / 0.4) * 4) - log2(0.2);

        double ne = nonUniformEntropy.report();
        assertEquals(expected, ne, 0.01d);
    }

    @Test
    public void testWithOLAAndSuppression() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        int k = 10;
        double suppression = 20.0;
        
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(suppression);
        ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        assertTrue(ola.reportSuppressionRate() > 0);
        
        NonUniformEntropy entropy = new NonUniformEntropy();
        entropy.initialize(original, anonymized, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformation, ola.reportBestNode().getValues(), null);

        assertNotEquals(0.0, entropy.report());
    }

    @Test
    public void testWithOLAAndSuppressionUpperBoundUnchanged() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        int[] kValues  = new int[] {2, 4, 5, 8, 10};
        double suppression = 20.0;

        double lastUpper = Double.NaN;
        
        for(int k: kValues) {
            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(suppression);
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

            NonUniformEntropy entropy = new NonUniformEntropy();
            entropy.initialize(original, anonymized, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformation, ola.reportBestNode().getValues(), null);
            
            if (!Double.isNaN(lastUpper)) {
                assertEquals(lastUpper, entropy.getUpperBound(), 0.000000001);
            }
            
            lastUpper = entropy.getUpperBound();
        }
    }
    
    @Test
    public void testNonUniformEntropyNoLoss() {

        IPVDataset original = new IPVDataset(List.of(
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4")),
                new SimpleSchema("foo", List.of(new SimpleSchemaField("Column 0", IPVSchemaFieldType.STRING))), true);

        IPVDataset anonymized = new IPVDataset(List.of(
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4")),
                new SimpleSchema("bar", List.of(new SimpleSchemaField("Column 0", IPVSchemaFieldType.STRING))), true);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());
        
        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);
        
        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                List.of(originalPartition), List.of(anonymizedPartition), columnInformationList, null);

        double ne = nonUniformEntropy.report();
        assertEquals(0.0, ne, 0.01);
    }

    @Test
    public void testNonUniformEntropyOneValue() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(List.of("1"));
        original.addRow(List.of("1"));
        original.addRow(List.of("1"));
        original.addRow(List.of("1"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(List.of("*"));
        anonymized.addRow(List.of("*"));
        anonymized.addRow(List.of("*"));
        anonymized.addRow(List.of("*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                List.of(originalPartition), List.of(anonymizedPartition), columnInformationList, null);

        double ne = nonUniformEntropy.report();
        assertEquals(0.0, ne, 0.01);
        assertEquals(0.0, nonUniformEntropy.getUpperBound(), 0.01);
    }
    
    @Test
    @Disabled("Dataset requires validation")
    public void testIssueWithUpperBound() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/13f496b9-6bd8-42e0-bbfe-44964d3b199e.csv");
        IPVDataset dataset = IPVDataset.load(is, true, ',', '"', false);

        try (InputStream confStream = this.getClass().getResourceAsStream("/13f496b9-6bd8-42e0-bbfe-44964d3b199e.json")) {
            AnonymizationOptions anonymizationOptions = new ObjectMapper().readValue(confStream, AnonymizationOptions.class);

            OLA ola = new OLA();
            ola.initialize(dataset, anonymizationOptions.getColumnInformation(), anonymizationOptions.getPrivacyConstraints(),
                    new OLAOptions(anonymizationOptions.getSuppressionRate()));

            IPVDataset anonymized = ola.apply();

            InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(dataset, anonymized, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(),
                    anonymizationOptions.getColumnInformation(), null);

            List<InformationLossResult> perQuasiColumn = nonUniformEntropy.reportPerQuasiColumn();

            for (InformationLossResult lossResult : perQuasiColumn) {
                assertFalse(lossResult.getValue() > lossResult.getUpperBound());
                assertFalse(lossResult.getValue() < lossResult.getLowerBound());
            }
        }
    }
}
