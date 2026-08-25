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
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MondrianSparkTest {
    private SparkSession spark;

    @Test
    public void testMedian() {
        List<Double> values = Arrays.asList(7.0, 9.0, 13.0, 12.0, 16.0);
        Dataset<Double> ds = spark.createDataset(values, Encoders.DOUBLE());
        assertEquals(12.0, MondrianSparkUtils.findMedian(ds)._3(), 0.0001);

        values = Arrays.asList(2.7, 3.5, 5.1, 8.3);
        assertEquals(4.3, MondrianSparkUtils.findMedian(spark.createDataset(values, Encoders.DOUBLE()))._3(), 0.00001);

        assertEquals(1.0, MondrianSparkUtils.findMedian(spark.createDataset(Arrays.asList(1.0), Encoders.DOUBLE()))._3(), 0.000001);
    }

    @BeforeEach
    public void setUp() {
        spark = SparkSession.builder()
                .master("local[1]")
                .appName("test")
                .config("spark.ui.enabled", "false")
                .config("spark.app.id", UUID.randomUUID().toString())
                .config("spark.driver.host", "localhost")
                .config("spark.sql.shuffle.partitions", "1")
                .getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        if (Objects.nonNull(spark)) {
            spark.stop();
        }
    }
}
