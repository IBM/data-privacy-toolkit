/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.dataset.reference;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}