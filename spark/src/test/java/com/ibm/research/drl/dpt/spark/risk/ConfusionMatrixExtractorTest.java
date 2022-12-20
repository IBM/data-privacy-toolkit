/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
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
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@Disabled("Require finalization of porting to Spark 3.2.3")
public class ConfusionMatrixExtractorTest {
    private SparkSession spark;
    private Dataset<Row> dummyDataset;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testNotAggregated() throws IOException {
        OutlierRemovalOptions configuration = mapper.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_not_aggregated.json"), OutlierRemovalOptions.class);
        Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                dummyDataset,
                configuration.getFilters()
        );

        assertThat(outputDataset.count(), is (dummyDataset.count()));
        assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));

        outputDataset.show();
    }


    @Test
    public void testOneAggregated() throws IOException {
        OutlierRemovalOptions configuration = mapper.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_one_aggregated.json"), OutlierRemovalOptions.class);

        Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                dummyDataset,
                configuration.getFilters()
        );

        assertThat(outputDataset.count(), is (dummyDataset.count()));
        assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));

        outputDataset.show();
    }

    @Test
    public void testTwoAggregatedSameID() throws IOException {
        try (InputStream inputStream = ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_two_aggregated_same_id.json")) {
            OutlierRemovalOptions configuration = mapper.readValue(inputStream, OutlierRemovalOptions.class);

            Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                    dummyDataset,
                    configuration.getFilters()
            );

            assertThat(outputDataset.count(), is(dummyDataset.count()));
            assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));
        }
    }

    @Test
    public void testTwoAggregatedDifferentID() throws IOException {
        OutlierRemovalOptions configuration = mapper.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_two_aggregated_different_id.json"), OutlierRemovalOptions.class);

        Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                dummyDataset,
                configuration.getFilters()
        );

        assertThat(outputDataset.count(), is (dummyDataset.count()));
        assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));

        outputDataset.show();
    }

    @Test
    public void testAggregatedAndNotAggregated() throws IOException {
        OutlierRemovalOptions configuration = mapper.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_aggregated_with_not_aggregated.json"), OutlierRemovalOptions.class);

        Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                dummyDataset,
                configuration.getFilters()
        );

        assertThat(outputDataset.count(), is (dummyDataset.count()));
        assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));

        outputDataset.show();
    }

    @Test
    public void testAggregatedMultipleFields() throws IOException {
        OutlierRemovalOptions configuration = mapper.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_one_aggregated_two_fields.json"), OutlierRemovalOptions.class);

        Dataset<Row> outputDataset = ConfusionMatrixExtractor.computeConfusionMatrix(
                dummyDataset,
                configuration.getFilters()
        );

        assertThat(outputDataset.count(), is (dummyDataset.count()));
        assertThat(outputDataset.columns().length, is(configuration.getFilters().size()));

        outputDataset.show();
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