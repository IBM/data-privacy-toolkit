/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.dataset.reference;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDatasetReferenceTest {
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
    public void testLocalFileRead() {
        FileDatasetReference reference = new FileDatasetReference(
                null,
                DataTypeFormat.CSV,
                new CSVDatasetOptions(false, ',', '\\', false),
                "", false
        );

        Dataset<Row> dataset = reference.readDataset(sparkSession, Objects.requireNonNull(FileDatasetReferenceTest.class.getResource("/dummyDataset.csv")).getPath());

        assertThat(dataset.count(), is(8L));
        assertThat(dataset.schema().fields().length, is(5));
    }

    @Test
    public void testLocalFileWriting() throws IOException {
        FileDatasetReference reference = new FileDatasetReference(
                null,
                DataTypeFormat.CSV,
                new CSVDatasetOptions(false, ',', '\\', false),
                "", false
        );

        Path tempFilePath  = Files.createTempFile("temp", "temp");

        assertTrue(Files.deleteIfExists(tempFilePath));
        assertThat(Files.exists(tempFilePath), is(false));

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

        reference.writeDataset(dataframe, tempFilePath.toAbsolutePath().toString());

        assertThat(Files.exists(tempFilePath), is(true));
    }
}