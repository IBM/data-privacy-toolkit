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
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ExtractFieldsStatisticsTest {
    private SparkSession spark;

    @BeforeEach
    public void setUp() {
        spark = SparkSession.builder().sparkContext(
                new SparkContext(
                        new SparkConf(true).
                                setMaster("local").
                                setAppName("testing").
                                set("spark.driver.bindAddress", "127.0.0.1")
                )
        ).getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        spark.stop();
    }

    @Test
    public void extractCardinalities() {
        Dataset<Row> dataset = createDataset(Arrays.asList(
                Arrays.asList( "A", false, 10 ),
                Arrays.asList( "B", true, 1000 ),
                Arrays.asList( "B", null, 200 ),
                Arrays.asList( "D", false, 11 )
        ), new StructType(new StructField[] {
                new StructField("f1", DataTypes.StringType, false, Metadata.empty()),
                new StructField("f2", DataTypes.BooleanType, true, Metadata.empty()),
                new StructField("f3", DataTypes.IntegerType, false, Metadata.empty())
        }));

        Map<String, Long> cardinality = ExtractFieldsStatistics.extractCardinalities(dataset);

        assertThat(cardinality.size(), is(3));
        assertThat(cardinality.containsKey("f1"), is(true));
        assertThat(cardinality.get("f1"), is(3L));
        assertThat(cardinality.containsKey("f2"), is(true));
        assertThat(cardinality.get("f2"), is(3L));
        assertThat(cardinality.containsKey("f3"), is(true));
        assertThat(cardinality.get("f3"), is(4L));
    }

    private Dataset<Row> createDataset(List<List<Object>> data, StructType schema) {
        return spark.createDataset(convertToRows(data), RowEncoder.apply(schema));
    }

    private List<Row> convertToRows(List<List<Object>> data) {
        return data.stream().map(Collection::toArray).map(RowFactory::create).collect(Collectors.toList());
    }
}