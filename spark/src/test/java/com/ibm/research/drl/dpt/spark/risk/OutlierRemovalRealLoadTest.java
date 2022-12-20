/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.apache.spark.sql.functions.col;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@Disabled("Require finalization of porting to Spark 3.2.3")
public class OutlierRemovalRealLoadTest {
    private static SparkSession spark;
    private static Dataset<Row> dummyDataset;

    @Test
    public void testNotAggregated() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_not_aggregated.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(1L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(dummyDataset.count() - 1L));
    }

    @Test
    public void testOneAggregated() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_one_aggregated.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(2L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(dummyDataset.count() - 2L));
    }

    @Test
    public void testTwoAggregatedSameID() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_two_aggregated_same_id.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(5L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(dummyDataset.count() - 5L));
    }

    @Test
    public void testTwoAggregatedDifferentID() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_two_aggregated_different_id.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(dummyDataset.count() - 1L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(1L));
    }

    @Test
    public void testAggregatedAndNotAggregated() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_aggregated_with_not_aggregated.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(dummyDataset.count() - 1L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(1L));
    }

    @Test
    public void testAggregatedMultipleFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/filter_one_aggregated_two_fields.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        assertThat(outputDataset.where(col("foo").equalTo(true)).count(), is(2L));
        assertThat(outputDataset.where(col("foo").equalTo(false)).count(), is(dummyDataset.count() - 2L));
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
        StructType schema = new StructType(new StructField[] {
                new StructField("de42_merch_id", DataTypes.StringType, false, Metadata.empty()),
                new StructField("de2_card_nbr", DataTypes.StringType, false, Metadata.empty()),
                new StructField("dw_net_pd_amt", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("dw_txn_cnt", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("dw_net_pd_cnt", DataTypes.IntegerType, false, Metadata.empty()),

        });
        dummyDataset = spark.read().schema(schema).csv(ConfusionMatrixExtractorTest.class.getResource("/dummyDataset.csv").getPath());
    }

    @AfterEach
    public void tearDown() {
        if (Objects.nonNull(spark)) {
            spark.stop();
        }
    }
}