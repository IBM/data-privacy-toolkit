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
package com.ibm.research.drl.dpt.spark.dataset.reference;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryDatasetReferenceTest {
    private SparkSession sparkSession;

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

        this.sparkSession = SparkSession.builder().sparkContext(new SparkContext(sparkConf)).getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        if (this.sparkSession != null) {
            this.sparkSession.stop();
        }
    }

    @Test
    public void testDataRead() {
        InMemoryDatasetReference datasetReference = new InMemoryDatasetReference(
                List.of(
                        List.of("FOO1", "BAR1"),
                        List.of("FOO2", "BAR2"),
                        List.of("FOO3", "BAR3"),
                        List.of("FOO4", "BAR4"),
                        List.of("FOO5", "BAR5")
                ), List.of("NAME", "SURNAME")
        );

        assertNotNull(datasetReference);

        Dataset<Row> dataframe = datasetReference.readDataset(sparkSession, "SOMETHING");

        assertNotNull(dataframe);

        assertThat(dataframe.count(), is(5L));
        assertThat(dataframe.columns().length, is(2));
    }

    @Test
    public void verifyWriting() {
        InMemoryDatasetReference datasetReference = new InMemoryDatasetReference();

        assertNotNull(datasetReference);

        Dataset<Row> dataframe = sparkSession.createDataset(
                List.of(RowFactory.create("FOO1", "BAR1"),
                        RowFactory.create("FOO2", "BAR2"),
                        RowFactory.create("FOO3", "BAR3"),
                        RowFactory.create("FOO4", "BAR4"),
                        RowFactory.create("FOO5", "BAR5")
                ), RowEncoder.apply(new StructType(new StructField[]{
                        new StructField("name", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("surname", DataTypes.StringType, false, Metadata.empty()),
                }))
        );

        datasetReference.writeDataset(dataframe, "FOOO");
    }

    @Test
    public void testDefensive() {
        assertThrows(IllegalArgumentException.class, () -> {
            InMemoryDatasetReference ignored = new InMemoryDatasetReference(
                    List.of(
                            List.of("A", "B"),
                            List.of("A", "B"),
                            List.of("A")
                    ), List.of("Col1", "Col2")
            );

            assertNotNull(ignored);
        });
    }
}
