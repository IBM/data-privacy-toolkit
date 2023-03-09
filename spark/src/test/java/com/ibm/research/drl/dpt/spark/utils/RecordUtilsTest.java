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