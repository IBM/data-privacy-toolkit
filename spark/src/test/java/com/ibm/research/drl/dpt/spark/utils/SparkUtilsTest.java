/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SparkUtilsTest {

    private SparkSession spark;

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
    
    @Test
    public void testCorrectCSVWithHeader() {
        Dataset<Row> dataset = SparkUtils.createDataset(spark, 
                SparkUtilsTest.class.getResource("/schematest.csv").getFile(), DataTypeFormat.CSV, new CSVDatasetOptions(true, ',', '"', false));
        
        StructType schema = dataset.schema();
        StructField[] fields = schema.fields();
        
        assertEquals(4, fields.length);
        
        assertEquals("id", fields[0].name());
        assertEquals("name", fields[1].name());
        assertEquals("email", fields[2].name());
        assertEquals("address", fields[3].name());
    }

    @Test
    public void testCorrectCSVNoHeader() {
        Dataset<Row> dataset = SparkUtils.createDataset(spark,
                SparkUtilsTest.class.getResource("/schematest.csv").getFile(), DataTypeFormat.CSV, new CSVDatasetOptions(false, ',', '"', false));

        StructType schema = dataset.schema();
        StructField[] fields = schema.fields();

        assertEquals(4, fields.length);

        assertEquals("Column 0", fields[0].name());
        assertEquals("Column 1", fields[1].name());
        assertEquals("Column 2", fields[2].name());
        assertEquals("Column 3", fields[3].name());
    }
    
    @Test
    public void testCreatesFieldNamesParquet() {
        Dataset<Row> dataset = SparkUtils.createDataset(spark,
                SparkUtilsTest.class.getResource("/schematest.parquet").getFile(), DataTypeFormat.PARQUET, null);

        List<String> fieldNames = SparkUtils.createFieldNames(dataset, DataTypeFormat.PARQUET, null);
        assertNotNull(fieldNames);
        assertEquals(4, fieldNames.size());
    }

    @Test
    public void testFilterDataset() {
        List<String> testData = Arrays.asList(
                "aaaaa",
                "bbbbb",
                "ccccc"
        );
        try (JavaSparkContext context = new JavaSparkContext(spark.sparkContext())) {
            JavaRDD<Row> rdd = context.parallelize(testData).map(value -> new GenericRow(new Object[]{value}));

            Dataset<Row> dataset = spark.createDataset(rdd.rdd(), RowEncoder.apply(new StructType(new StructField[]{
                    new StructField("column", DataTypes.StringType, false, Metadata.empty())
            })));

            assertThat(SparkUtils.filterOnFieldValue(dataset, "column", "a{5}").count(), is(1L));
            assertThat(SparkUtils.filterOnFieldValue(dataset, "column", "[ab]{5}").count(), is(2L));
        }
    }

    @Test
    public void testFilterDatasetFieldsOtherThanString() {
        List<Object> testData = Arrays.asList(
                11111,
                22222,
                33333,
                44444
        );
        try (JavaSparkContext context = new JavaSparkContext(spark.sparkContext())) {
            JavaRDD<Row> rdd = context.parallelize(testData).map(value -> new GenericRow(new Object[]{value}));


            Dataset<Row> dataset = spark.createDataset(rdd.rdd(), RowEncoder.apply(new StructType(new StructField[]{
                    new StructField("column", DataTypes.IntegerType, false, Metadata.empty())
            })));

            assertThat(SparkUtils.filterOnFieldValue(dataset, "column", "a{5}").count(), is(0L));
            assertThat(SparkUtils.filterOnFieldValue(dataset, "column", "1{5}").count(), is(1L));
            assertThat(SparkUtils.filterOnFieldValue(dataset, "column", "[12]{5}").count(), is(2L));
        }
    }
}