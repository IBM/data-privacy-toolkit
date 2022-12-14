/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
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
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("2"));
        original.addRow(Arrays.asList("3"));
        original.addRow(Arrays.asList("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("3-4")));
        anonymized.addRow(Arrays.asList(new String("3-4")));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);
        
        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized, 
                Arrays.asList(originalPartition), Arrays.asList(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));
        
        Double ne = nonUniformEntropy.report();

        assertEquals(4.0, ne, 0.01);
        assertEquals(upperBound, nonUniformEntropy.getUpperBound(), 0.01);
    }

    @Test
    public void testNonUniformEntropyWithWeights() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("2"));
        original.addRow(Arrays.asList("3"));
        original.addRow(Arrays.asList("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("3-4")));
        anonymized.addRow(Arrays.asList(new String("3-4")));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI, 0.5));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                Arrays.asList(originalPartition), Arrays.asList(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));

        Double ne = nonUniformEntropy.report();

        assertEquals(4.0 * 0.5, ne, 0.01);
        assertEquals(upperBound, nonUniformEntropy.getUpperBound(), 0.01);
    }

    @Test
    public void testNonUniformEntropyWithZeroWeights() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("2"));
        original.addRow(Arrays.asList("3"));
        original.addRow(Arrays.asList("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("3-4")));
        anonymized.addRow(Arrays.asList(new String("3-4")));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI, 0));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                Arrays.asList(originalPartition), Arrays.asList(anonymizedPartition), columnInformationList, null);

        double upperBound = 4 * (-log2(0.25));

        Double ne = nonUniformEntropy.report();

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
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("2"));
        original.addRow(Arrays.asList("3"));
        original.addRow(Arrays.asList("4"));
        original.addRow(Arrays.asList("5"));

        Partition originalPartition1 = new InMemoryPartition(1);
        originalPartition1.getMember().addRow(Arrays.asList("1"));
        originalPartition1.getMember().addRow(Arrays.asList("2"));
        originalPartition1.getMember().addRow(Arrays.asList("3"));
        originalPartition1.getMember().addRow(Arrays.asList("4"));
        originalPartition1.setAnonymous(true);

        Partition originalPartition2 = new InMemoryPartition(1);
        originalPartition2.getMember().addRow(Arrays.asList("5"));
        originalPartition2.setAnonymous(false);
       
        List<Partition> originalPartitions = new ArrayList<>();
        originalPartitions.add(originalPartition1);
        originalPartitions.add(originalPartition2);
        
        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("1-2")));
        anonymized.addRow(Arrays.asList(new String("3-4")));
        anonymized.addRow(Arrays.asList(new String("3-4")));
        anonymized.addRow(Arrays.asList(new String("5-6")));

        Partition anonymizedPartition1 = new InMemoryPartition(1);
        anonymizedPartition1.getMember().addRow(Arrays.asList("1-2"));
        anonymizedPartition1.getMember().addRow(Arrays.asList("1-2"));
        anonymizedPartition1.getMember().addRow(Arrays.asList("3-4"));
        anonymizedPartition1.getMember().addRow(Arrays.asList("3-4"));
        anonymizedPartition1.setAnonymous(true);

        Partition anonymizedPartition2 = new InMemoryPartition(1);
        anonymizedPartition2.getMember().addRow(Arrays.asList("5-6"));
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

        Double lastUpper = null;
        
        for(int k: kValues) {
            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(suppression);
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

            NonUniformEntropy entropy = new NonUniformEntropy();
            entropy.initialize(original, anonymized, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformation, ola.reportBestNode().getValues(), null);
            
            System.out.println(entropy.getLowerBound() + ":" + entropy.getUpperBound());
            
            if (lastUpper != null) {
                assertEquals(lastUpper.doubleValue(), entropy.getUpperBound(), 0.000000001);
            }
            
            lastUpper = entropy.getUpperBound();
        }
    }
    
    @Test
    public void testNonUniformEntropyNoLoss() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("2"));
        original.addRow(Arrays.asList("3"));
        original.addRow(Arrays.asList("4"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList("1"));
        anonymized.addRow(Arrays.asList("2"));
        anonymized.addRow(Arrays.asList("3"));
        anonymized.addRow(Arrays.asList("4"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());
        
        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);
        
        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized, 
                Arrays.asList(originalPartition), Arrays.asList(anonymizedPartition), columnInformationList, null);

        Double ne = nonUniformEntropy.report();
        assertEquals(0.0, ne, 0.01);
    }

    @Test
    public void testNonUniformEntropyOneValue() {

        IPVDataset original = new IPVDataset(1);
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("1"));
        original.addRow(Arrays.asList("1"));

        IPVDataset anonymized = new IPVDataset(1);
        anonymized.addRow(Arrays.asList("*"));
        anonymized.addRow(Arrays.asList("*"));
        anonymized.addRow(Arrays.asList("*"));
        anonymized.addRow(Arrays.asList("*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));

        Partition originalPartition = new InMemoryPartition(original.getValues());
        Partition anonymizedPartition = new InMemoryPartition(anonymized.getValues());

        originalPartition.setAnonymous(true);
        anonymizedPartition.setAnonymous(true);

        InformationMetric nonUniformEntropy = new NonUniformEntropy().initialize(original, anonymized,
                Arrays.asList(originalPartition), Arrays.asList(anonymizedPartition), columnInformationList, null);

        Double ne = nonUniformEntropy.report();
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