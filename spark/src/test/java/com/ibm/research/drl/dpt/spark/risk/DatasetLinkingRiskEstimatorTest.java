/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
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
import java.io.OutputStream;
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
        String filename = "/tmp/testDistinctLevels.csv";

        try (OutputStream os = new FileOutputStream(filename)) {
            IOUtils.copy(DatasetLinkingRiskEstimatorTest.class.getResourceAsStream("/testDistinctLevels.csv"), os);
        }
        
        Dataset<Row> dataset = spark.read().option("header", "true").csv(filename);

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
        String filename = "/tmp/testExtractDataset.csv";

        try (OutputStream os = new FileOutputStream(filename);) {
            IOUtils.copy(DatasetLinkingRiskEstimatorTest.class.getResourceAsStream("/testDistinctLevels.csv"), os);
        }

        Dataset<Row> dataset = spark.read().option("header", "true").csv(filename);

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
        String filename = "/tmp/testDistinctLevels2.csv";

        try (OutputStream os = new FileOutputStream(filename);) {
            IOUtils.copy(this.getClass().getResourceAsStream("/testDistinctLevels.csv"), os);
        }

        Dataset<Row> dataset = spark.read().option("header", "true").csv(filename);

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