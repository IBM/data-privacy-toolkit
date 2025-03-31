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
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.informationloss.CategoricalPrecision;
import com.ibm.research.drl.dpt.anonymization.informationloss.NonUniformEntropy;
import com.ibm.research.drl.dpt.anonymization.ola.LatticeNode;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Disabled
public class EstimatedRiskTest {
    private static final int POPULATION = 13_511_855;
 
    @Test
    @Disabled
    public void testIsolatedCase() {
        int Fk = 432;
        double p = 11.0 / (double) Fk;

        final BinomialDistribution binomialDistribution = new BinomialDistribution(Fk, p);

        for (int i = 0; i < 10; i++) {
            double value = binomialDistribution.probability(1);
            System.out.println(value);
        }

        final HypergeometricDistribution distribution = new HypergeometricDistribution(Fk, 11, 11);
        double k_risk = distribution.probability(1);
        System.out.println(k_risk);
    }
   
    @Test
    @Disabled
    public void testQuickCheck() {
        int Fk = 1140;
        final BinomialDistribution binomialDistribution = new BinomialDistribution(Fk, 11.0/(double)Fk);
        double value = binomialDistribution.probability(1);
        System.out.println(value);
        
    }

    @Test
    @Disabled
    public void testQuickCheck2() {
        double fk = 11;
        double Fk = 432;
        
        double pk = fk / Fk;
        double qk = 1.0 - pk;
        
        double value = (pk / fk) *(1 + (qk / (fk + 1)) + 2 * Math.pow(qk, 2) / ((fk + 1) * (fk + 2)));  
        
        System.out.println(value);

    }
    
    @Test
    @Disabled
    public void testDumpAnonymizedInput() throws Exception {
        LatticeNode node = new LatticeNode(new int[]{2, 0, 0, 1});
        
        try (InputStream population = EstimatedRiskTest.class.getResourceAsStream("/florida_original.txt");) {
            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                    ColumnType.QUASI, true));

            try (FileWriter fileWriter = new FileWriter("/tmp/florida_anonymized.txt");) {
                DatasetGeneralizer.generalizeCSVInputStream(population, fileWriter, columnInformation, node.getValues());
            }
        }
    }
    
    private void writeDataset(IPVDataset dataset, String filename) throws Exception {
        
        int numberOfRows = dataset.getNumberOfRows();
        FileWriter fileWriter = new FileWriter(filename);
        
        for(int i = 0; i < numberOfRows; i++) {
            List<String> anonymizedRow = dataset.getRow(i); 

            StringWriter stringWriter = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.RFC4180.withDelimiter(',').withQuoteMode(QuoteMode.MINIMAL));
            csvPrinter.printRecord(anonymizedRow);
            String anonRow = stringWriter.toString().trim();

            fileWriter.write(anonRow);
            fileWriter.write('\n');
        }
        
        fileWriter.close();
    }
    
    @Test
    @Disabled
    public void testAccuracy() throws Exception {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")), 
                ColumnType.QUASI, true));
        System.out.println("columnInformation done");

        int[] kValues = {2, 5, 10, 15, 20, 50, 100};
        double[] suppressionValues = {0.0, 2.0, 5.0, 10.0};
        
        List<String> resources = new ArrayList<>();
        for(int i = 1; i <= 50; i++) {
            String filename = "/florida_samples/sample" + i;
            resources.add(filename);
            try (InputStream sample = EstimatedRiskTest.class.getResourceAsStream(filename);) {
                assertNotNull(sample);
            }
        }
        
        
        for(int k: kValues) {
            for(double suppression: suppressionValues) {
               
                Map<String, List<Double>> riskValues = new HashMap<>();
                
                riskValues.put("binomial", new ArrayList<>());
                riskValues.put("approximation", new ArrayList<>());
                riskValues.put("fk", new ArrayList<>());
                riskValues.put("real", new ArrayList<>());
               
                LatticeNode lastNode = null;
                Map<String, Integer> lastCounters = null;
                
                for(String resource: resources) {
                    try (InputStream sample = EstimatedRiskTest.class.getResourceAsStream(resource);) {
                        IPVDataset sampleDataset = IPVDataset.load(sample, false, ',', '"', false);

                        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                        privacyConstraints.add(new KAnonymity(k));

                        OLAOptions olaOptions = new OLAOptions(suppression);
                        OLA ola = new OLA();
                        ola.initialize(sampleDataset, columnInformation, privacyConstraints, olaOptions);

                        IPVDataset anonymizedSampleDataset = ola.apply();

                        Map<String, Integer> anonEQCounters = AnonymizationUtils.generateEQCounters(anonymizedSampleDataset, columnInformation);

                        BinomialRiskMetric risk = new BinomialRiskMetric();
                        Map<String, String> options = new HashMap<>(2);
                        options.put(BinomialRiskMetric.POPULATION, Integer.toString(POPULATION));
                        options.put(BinomialRiskMetric.USE_GLOBAL_P, Boolean.toString(Boolean.FALSE));
                        risk.initialize(null, anonymizedSampleDataset, columnInformation, k, options);

                        ApproximationRiskMetric approximationRiskMetric = new ApproximationRiskMetric();
                        Map<String, String> approximateRiskOptions = new HashMap<>(2);
                        approximateRiskOptions.put(ApproximationRiskMetric.POPULATION, Integer.toString(POPULATION));
                        approximateRiskOptions.put(ApproximationRiskMetric.USE_GLOBAL_P, Boolean.toString(Boolean.FALSE));
                        approximationRiskMetric.initialize(null, anonymizedSampleDataset, columnInformation, k, approximateRiskOptions);

                        FKRatioMetric fkRatioMetric = new FKRatioMetric();
                        Map<String, String> fkRatioOptions = new HashMap<>(2);
                        fkRatioOptions.put(FKRatioMetric.POPULATION, Integer.toString(POPULATION));
                        fkRatioMetric.initialize(null, anonymizedSampleDataset, columnInformation, k, fkRatioOptions);

                        Map<String, Integer> originalEQCounters;

                        if (lastNode != null && lastNode.equals(ola.reportBestNode())) {
                            originalEQCounters = lastCounters;
                        } else {
                            try (InputStream population = EstimatedRiskTest.class.getResourceAsStream("/florida_original.txt");) {
                                originalEQCounters = DatasetGeneralizer.generalizeCSVAndCountEQ(population, columnInformation, ola.reportBestNode().getValues());
                            }
                        }

                        lastNode = ola.reportBestNode();
                        lastCounters = originalEQCounters;

                        int minEQSize = Integer.MAX_VALUE;
                        for (Map.Entry<String, Integer> entry : originalEQCounters.entrySet()) {
                            Integer counter = entry.getValue();

                            if (counter >= k) {
                                if (anonEQCounters.containsKey(entry.getKey()) && counter < minEQSize) {
                                    minEQSize = counter;
                                }
                            }
                        }

                        double binomialRisk = risk.report();
                        riskValues.get("binomial").add(binomialRisk);

                        double approximationRisk = approximationRiskMetric.report();
                        riskValues.get("approximation").add(approximationRisk);

                        double fkRisk = fkRatioMetric.report();
                        riskValues.get("fk").add(fkRisk);

                        double realRisk = (1.0 / (double) minEQSize);
                        riskValues.get("real").add(realRisk);
                    }

                    System.out.printf("%d\t%f", k, suppression);

                    for (Map.Entry<String, List<Double>> entry : riskValues.entrySet()) {
                        String key = entry.getKey();
                        List<Double> values = entry.getValue();
                        System.out.printf("\t%s\t%f\t%f\t%f", key,
                                Collections.min(values), Collections.max(values), values.stream().mapToDouble(Double::doubleValue).sum() / (double) values.size());
                    }
                    System.out.println();
                }
            }
        }
    }

    @Test
    @Disabled
    public void testDistCheck() throws Exception {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));
        System.out.println("columnInformation done");

        //0:1:2:1
        LatticeNode node = new LatticeNode(new int[]{0, 1, 2, 1});
        
        try (InputStream population = EstimatedRiskTest.class.getResourceAsStream("/florida_original.txt");) {
            Map<String, Integer> originalEQCounters = DatasetGeneralizer.generalizeCSVAndCountEQ(population, columnInformation, node.getValues());

            for (Map.Entry<String, Integer> entry : originalEQCounters.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }
    }
    
    
    
    @Test
    public void test() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);

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

        double suppression = 15.0;

        int k = 10;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));


        OLAOptions olaOptions = new OLAOptions(suppression);
        OLA ola = new OLA();
        ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        BinomialRiskMetric risk = new BinomialRiskMetric();

        Map<String, String> options = new HashMap<>(2);
        options.put(BinomialRiskMetric.POPULATION, Integer.toString(189_000));
        options.put(BinomialRiskMetric.USE_GLOBAL_P, Boolean.toString(Boolean.FALSE));

        risk.initialize(original, anonymized, ola.getColumnInformationList(), k, options);

//        for (int i = 0; i < 10; i++) {
//            System.out.println(risk.report());
//        }
    }

    @Test
    @Disabled
    public void testEstimation() throws Exception {
        try (InputStream inputStream = EstimatedRiskTest.class.getResourceAsStream("/random1.txt")) {
            IPVDataset original = IPVDataset.load(inputStream, false, ',', '"', false);

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

            double suppression = 15.0;

            int k = 10;
            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));


            OLAOptions olaOptions = new OLAOptions(suppression);
            OLA ola = new OLA();
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();


            final int N = POPULATION;
            final double n = anonymized.getNumberOfRows();
            for (int t = 0; t < 30; ++t) {
                int tot = 0;
                for (final Partition partition : ola.getOriginalPartitions()) {
                    if (partition.size() < k) continue;

                    final double pi_k = partition.size() / n;

                    tot += new PoissonDistribution(N * pi_k).sample();

                }

                System.out.println(Math.abs(tot - N));
            }
        }
    }

    @Test
    @Disabled
    public void testDump() throws Exception {
        InputStream sample = EstimatedRiskTest.class.getResourceAsStream("/florida_sample_0.01.txt");
        IPVDataset sampleDataset = IPVDataset.load(sample, false, ',', '"', false);

        System.out.println("loading done");


        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));
        System.out.println("columnInformation done");

        int[] kValues = {2,};
        double[] suppressionValues = {2.0};

        for(int k: kValues) {
            for(double suppression: suppressionValues) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLAOptions olaOptions = new OLAOptions(suppression);
                OLA ola = new OLA();
                ola.initialize(sampleDataset, columnInformation, privacyConstraints, olaOptions);

                IPVDataset anonymizedSampleDataset = ola.apply();

                writeDataset(anonymizedSampleDataset, "/tmp/florida_sample_0.01_anon_" + k + "_" + suppression  + ".csv"); 
            }
        }
    }

    @Test
    @Disabled
    public void testAccuracyB() throws Exception {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));
        System.out.println("columnInformation done");

        int[] kValues = {2, 5, 10, 15, 20, 25, 30, 50, 100};
        double[] suppressionValues = {2.0};

        List<String> resources = new ArrayList<>();
        for(int i = 3; i <= 3; i++) {
            String filename = "/florida_samples/sample" + i;
            resources.add(filename);
            InputStream sample = this.getClass().getResourceAsStream(filename);
            assertNotNull(sample);
            sample.close();
        }


        for(int k: kValues) {
            for(double suppression: suppressionValues) {

                Map<String, List<Double>> riskValues = new HashMap<>();

                riskValues.put("binomial", new ArrayList<>());
                riskValues.put("cp", new ArrayList<>());
                riskValues.put("nue", new ArrayList<>());

                for(String resource: resources) {
                    InputStream sample = this.getClass().getResourceAsStream(resource);
                    IPVDataset sampleDataset = IPVDataset.load(sample, false, ',', '"', false);

                    List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                    privacyConstraints.add(new KAnonymity(k));

                    OLAOptions olaOptions = new OLAOptions(suppression);
                    OLA ola = new OLA();
                    ola.initialize(sampleDataset, columnInformation, privacyConstraints, olaOptions);

                    IPVDataset anonymizedSampleDataset = ola.apply();

                    BinomialRiskMetric risk = new BinomialRiskMetric();
                    Map<String, String> options = new HashMap<>(2);
                    options.put(BinomialRiskMetric.POPULATION, Integer.toString(POPULATION));
                    options.put(BinomialRiskMetric.USE_GLOBAL_P, Boolean.toString(Boolean.FALSE));
                    risk.initialize(null, anonymizedSampleDataset, columnInformation, k, options);

                    double binomialRisk = risk.report();
                    riskValues.get("binomial").add(binomialRisk);
                    
                    CategoricalPrecision categoricalPrecision = new CategoricalPrecision();
                    categoricalPrecision.initialize(sampleDataset, anonymizedSampleDataset, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformation, null);
                    double iloss = categoricalPrecision.report();
                    riskValues.get("cp").add(iloss);

                    NonUniformEntropy nonUniformEntropy = new NonUniformEntropy();
                    nonUniformEntropy.initialize(sampleDataset, anonymizedSampleDataset, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformation, null);
                    double nueLoss = nonUniformEntropy.report();
                    riskValues.get("nue").add(nueLoss);
                    
                    sample.close();
                }

                System.out.printf("%d\t%f", k, suppression);

                for(Map.Entry<String, List<Double>> entry: riskValues.entrySet()) {
                    String key = entry.getKey();
                    List<Double> values = entry.getValue();
                    System.out.printf("\t%s\t%f\t%f\t%f", key,
                            Collections.min(values), Collections.max(values), values.stream().mapToDouble(Double::doubleValue).sum() / (double) values.size());
                }
                System.out.println();

            }
        }
    }
}

