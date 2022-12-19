/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RecordUtilsTest {

    private SparkSession spark;

    @BeforeEach
    public void setUp() {
        spark = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true).
                setMaster("local[*]").
                setAppName("RecordUtilsTest").set("spark.driver.host", "localhost"))).getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        spark.stop();
    }

    @Test
    public void testCreateFieldMapCSV() {
        Dataset<Row> dataset = spark.read().option("header", "true").csv(this.getClass().getResource("/schematest.csv").getFile());
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());
      
        assertEquals(4, fieldMap.size());
        assertEquals(0, fieldMap.get("id").intValue());
        assertEquals(1, fieldMap.get("name").intValue());
        assertEquals(2, fieldMap.get("email").intValue());
        assertEquals(3, fieldMap.get("address").intValue());
    }

    @Test
    public void testCreateFieldCSVNoHeader() {
        Dataset<Row> dataset = spark.read().csv(this.getClass().getResource("/schematest.csv").getFile());
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());

        assertEquals(4, fieldMap.size());

        for(Map.Entry<String, Integer> entry: fieldMap.entrySet()) {
            System.out.println(String.format("%s: %d", entry.getKey(), entry.getValue()));
        }
    }

    @Test
    public void testCreateFieldMapParquet() {
        Dataset<Row> dataset = spark.read().parquet(this.getClass().getResource("/schematest.parquet").getFile());
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());

        assertEquals(4, fieldMap.size());
        
        for(Map.Entry<String, Integer> entry: fieldMap.entrySet()) {
            System.out.println(String.format("%s: %d", entry.getKey(), entry.getValue()));
        }
    }
}