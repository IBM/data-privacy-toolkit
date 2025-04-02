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
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.vulnerability.brute.Brute;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KMeansAnonymizationTest {

    @Test
    public void testSuppressionRate() throws Exception {
        int k = 4;
        double maxSuppressionRate = 1.0;

        InputStream is = this.getClass().getResourceAsStream("/100.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<>();
        for(int i = 0; i < csvDataset.getNumberOfColumns(); i++) {
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(csvDataset, i, ColumnType.QUASI));
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, 
                new KMeansOptions(maxSuppressionRate, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertTrue(anonymized.getNumberOfRows() > 0);
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);
        
        double suppressionRate = kMeansAnonymization.reportSuppressionRate();
        assertTrue(suppressionRate <= maxSuppressionRate);
    }

    @Test
    public void testZeroSuppressionRate() throws Exception {
        int k = 4;
        double maxSuppressionRate = 0.0;

        InputStream is = this.getClass().getResourceAsStream("/100.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<>();
        for(int i = 0; i < csvDataset.getNumberOfColumns(); i++) {
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(csvDataset, i, ColumnType.QUASI));
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints,
                new KMeansOptions(maxSuppressionRate, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertTrue(anonymized.getNumberOfRows() > 0);
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);
        
        double suppressionRate = kMeansAnonymization.reportSuppressionRate();
        assertEquals(0.0, suppressionRate, 0.000000001);
    }
    
    @Test
    public void testNumerical() throws Exception {
        int k = 4;

        InputStream is = this.getClass().getResourceAsStream("/100.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        for(int i = 0; i < csvDataset.getNumberOfColumns(); i++) {
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(csvDataset, i, ColumnType.QUASI));
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, new KMeansOptions(100.0, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertTrue(anonymized.getNumberOfRows() > 0);
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);
    }

    @Test
    public void testCategorical() throws Exception {
        int k = 4;

        InputStream is = this.getClass().getResourceAsStream("/testCategoricalOriginal.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "*");
        hierarchy.add("Manager", "*");
        hierarchy.add("Director", "*");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, new KMeansOptions(100.0, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertNotNull(anonymized);

        Brute brute = new Brute(k);
        assertEquals(0, brute.apply(anonymized).size());
    }

    @Test
    public void testCategorical2() throws Exception {
        int k = 4;

        InputStream is = this.getClass().getResourceAsStream("/testCategoricalKmeans.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "*");
        hierarchy.add("Manager", "*");
        hierarchy.add("Director", "*");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, new KMeansOptions(100.0, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertNotNull(anonymized);

        Brute brute = new Brute(k);
        assertEquals(0, brute.apply(anonymized).size());
    }

    @Test
    public void testMix() throws Exception {
        int k = 4;

        InputStream is = this.getClass().getResourceAsStream("/testOLA.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<>();
        for(int i = 0; i < csvDataset.getNumberOfColumns(); i++) {
            columnInformationList.add(ColumnInformationGenerator.generateCategoricalFromData(csvDataset, i, ColumnType.QUASI));
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, new KMeansOptions(100.0, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertNotNull(anonymized);

        Brute brute = new Brute(k);
        assertEquals(0, brute.apply(anonymized).size());
    }


    @Test
    public void testIgnoresNonQuasi() throws Exception {
        int k = 4;

        InputStream is = this.getClass().getResourceAsStream("/100_with_id.csv");
        IPVDataset csvDataset = IPVDataset.load(is, false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(csvDataset, 1, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(csvDataset, 2, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        KMeansAnonymization kMeansAnonymization = new KMeansAnonymization();
        kMeansAnonymization.initialize(csvDataset, columnInformationList, privacyConstraints, new KMeansOptions(100.0, StrategyOptions.DUMMY));

        IPVDataset anonymized = kMeansAnonymization.apply();

        assertNotNull(anonymized);

        //assertEquals(csvDataset.getNumberOfRows(), anonymized.getNumberOfRows());
        assertEquals(csvDataset.getNumberOfColumns(), anonymized.getNumberOfColumns());


        Map<String, String> nonquasiContents = new HashMap<>();

        for(int i = 0; i < csvDataset.getNumberOfRows(); i++) {
            String rowID = csvDataset.get(i, 0);

            StringBuilder builder  = new StringBuilder();
            for(int j = 3; j < csvDataset.getNumberOfColumns(); j++) {
                builder.append(csvDataset.get(i, j));
                builder.append("::");
            }

            nonquasiContents.put(rowID, builder.toString());
        }

        //we check that we dont have vulnerabilities on the quasi-identifiers
        IPVDataset dataset = new IPVDataset(new ArrayList<>(), IPVDataset.generateSchemaWithoutColumnNames(2), false);
        for(int i = 0; i < anonymized.getNumberOfRows(); i++) {
            List<String> row = new ArrayList<>();

            row.add(anonymized.get(i, 1));
            row.add(anonymized.get(i, 2));

            dataset.addRow(row);

            String rowID = anonymized.get(i, 0);

            StringBuilder builder  = new StringBuilder();
            for(int j = 3; j < anonymized.getNumberOfColumns(); j++) {
                builder.append(anonymized.get(i, j));
                builder.append("::");
            }

            assertEquals(nonquasiContents.get(rowID), builder.toString());
        }

        Brute brute = new Brute(k);
        assertEquals(0, brute.apply(dataset).size());
    }
}
