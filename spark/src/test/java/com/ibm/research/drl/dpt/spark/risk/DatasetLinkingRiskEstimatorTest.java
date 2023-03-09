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
package com.ibm.research.drl.dpt.spark.risk;

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DatasetLinkingRiskEstimatorTest {
    private SparkSession spark;

    @Test
    public void testFilterDistinctGeneralizationLevelsNoAnonymisedColumns() throws Exception {
        List<BasicColumnInformation> basicColumnInformation = new ArrayList<>();
        basicColumnInformation.add(new BasicColumnInformation("Gender", "Gender", false, (GeneralizationHierarchy)null));
        basicColumnInformation.add(new BasicColumnInformation("Race", "Race", false, (GeneralizationHierarchy)null));

        List<List<Integer>> distinctLevels = DatasetLinkingRiskEstimator.filterDistinctGeneralizationLevels(null, basicColumnInformation);
        assertEquals(1, distinctLevels.size()); 
        assertEquals(basicColumnInformation.size(), distinctLevels.get(0).size());
        
        for(Integer value: distinctLevels.get(0)) {
            assertEquals(0, value.intValue());
        }
    }
    
    @Test
    public void testFilterDistinctGeneralizationLevels() throws Exception {
        Path tempFile = Files.createTempFile("temp", "distinct-Levels");

        try (
                OutputStream output = new FileOutputStream(tempFile.toString());
                InputStream input = DatasetLinkingRiskEstimatorTest.class.getResourceAsStream("/testDistinctLevels.csv");
        ) {
            IOUtils.copy(input, output);
        }
        
        Dataset<Row> dataset = spark.read().option("header", "true").csv(tempFile.toString());

        GeneralizationHierarchy genderHierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female"), "*");
        GeneralizationHierarchy raceHierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("White"), "*");
        
        List<BasicColumnInformation> basicColumnInformation = new ArrayList<>();
        basicColumnInformation.add(new BasicColumnInformation("Gender", "Gender", true, genderHierarchy));
        basicColumnInformation.add(new BasicColumnInformation("Race", "Race", true, raceHierarchy));
        
        List<List<Integer>> distinctLevels = DatasetLinkingRiskEstimator.filterDistinctGeneralizationLevels(dataset, basicColumnInformation);
        assertEquals(4, distinctLevels.size());
        assertTrue(distinctLevels.contains(Arrays.asList(0, 0)));
        assertTrue(distinctLevels.contains(Arrays.asList(0, 1)));
        assertTrue(distinctLevels.contains(Arrays.asList(1, 0)));
        assertTrue(distinctLevels.contains(Arrays.asList(1, 1)));
    }
    
    @Test
    public void testExtractDataset() throws Exception {
        Path tempFile = Files.createTempFile("temp", "distinct-Levels");

        try (
                OutputStream output = new FileOutputStream(tempFile.toString());
                InputStream input = DatasetLinkingRiskEstimatorTest.class.getResourceAsStream("/testDistinctLevels.csv");
        ) {
            IOUtils.copy(input, output);
        }

        Dataset<Row> dataset = spark.read().option("header", "true").csv(tempFile.toString());

        GeneralizationHierarchy genderHierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female"), "*");
        GeneralizationHierarchy raceHierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("White"), "*");
        
        List<BasicColumnInformation> basicColumnInformation = new ArrayList<>();
        basicColumnInformation.add(new BasicColumnInformation("Gender", "Gender", true, genderHierarchy));
        basicColumnInformation.add(new BasicColumnInformation("Race", "Race", true, raceHierarchy));
        
        Dataset<Row> extracted = DatasetLinkingRiskEstimator.extractDataset(dataset, Arrays.asList(0, 0), 2, basicColumnInformation);
        
        assertEquals(2, extracted.count());
        
        List<Row> rows = extracted.collectAsList();
        assertEquals("White", rows.get(0).getString(1));
        assertEquals("White", rows.get(1).getString(1));
    }

    @Test
    public void testFilterDistinctGeneralizationLevels2() throws Exception {
        Path tempFile = Files.createTempFile("temp", "distinct-Levels");

        try (
                OutputStream output = new FileOutputStream(tempFile.toString());
                InputStream input = DatasetLinkingRiskEstimatorTest.class.getResourceAsStream("/testDistinctLevels.csv");
        ) {
            IOUtils.copy(input, output);
        }

        Dataset<Row> dataset = spark.read().option("header", "true").csv(tempFile.toString());

        GeneralizationHierarchy genderHierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female"), "*");

        List<BasicColumnInformation> basicColumnInformation = new ArrayList<>();
        basicColumnInformation.add(new BasicColumnInformation("Gender", "Gender", true, genderHierarchy));
        basicColumnInformation.add(new BasicColumnInformation("Race", "Race", false, (GeneralizationHierarchy)null));

        List<List<Integer>> distinctLevels = DatasetLinkingRiskEstimator.filterDistinctGeneralizationLevels(dataset, basicColumnInformation);
        assertEquals(2, distinctLevels.size());
        assertTrue(distinctLevels.contains(Arrays.asList(0)));
        assertTrue(distinctLevels.contains(Arrays.asList(1)));
    }
    
    @Test
    public void testCountAnonymisedColumns() {
        List<BasicColumnInformation> basicColumnInformation = new ArrayList<>();
        basicColumnInformation.add(new BasicColumnInformation("foo1", "t1", true, (GeneralizationHierarchy)null));
        basicColumnInformation.add(new BasicColumnInformation("foo2", "t2", true, (GeneralizationHierarchy)null));
        basicColumnInformation.add(new BasicColumnInformation("foo3", "t3", false, (GeneralizationHierarchy)null));
        basicColumnInformation.add(new BasicColumnInformation("foo4", "t4", true, (GeneralizationHierarchy)null));
       
        assertEquals(3, DatasetLinkingRiskEstimator.countAnonymisedColumns(basicColumnInformation));
    }
    
    @Test
    public void testConvertToList() {
        String levelStr = "1:2:3:";
        List<Integer> levels = DatasetLinkingRiskEstimator.convertToList(levelStr);
        
        assertEquals(3, levels.size());
        assertEquals(1, levels.get(0).intValue());
        assertEquals(2, levels.get(1).intValue());
        assertEquals(3, levels.get(2).intValue());

        levelStr = "1:2:3";
        levels = DatasetLinkingRiskEstimator.convertToList(levelStr);

        assertEquals(3, levels.size());
        assertEquals(1, levels.get(0).intValue());
        assertEquals(2, levels.get(1).intValue());
        assertEquals(3, levels.get(2).intValue());
    }

    @BeforeEach
    public void setUp() {
        SparkConf sparkConf =
                (new SparkConf())
                        .setMaster("local[1]")
                        .setAppName("test")
                        .set("spark.ui.enabled", "false")
                        .set("spark.app.id", UUID.randomUUID().toString())
                        .set("spark.driver.host", "localhost")
                        .set("spark.sql.shuffle.partitions", "1");

        spark = SparkSession.builder().sparkContext(new SparkContext(sparkConf)).getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        if (Objects.nonNull(spark)) {
            spark.stop();
        }
    }
}