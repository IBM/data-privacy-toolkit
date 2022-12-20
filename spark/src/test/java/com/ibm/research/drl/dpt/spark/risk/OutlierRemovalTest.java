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
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Disabled("Missing test data in git")
public class OutlierRemovalTest {
    private SparkSession spark;
    private Dataset<Row> inputDataset;

    @Test
    public void testNoAggregation() {
        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                inputDataset,
                Arrays.asList(
                        new OutlierRemovalFilter(
                                Collections.emptyList(),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.NOT_AGGREGATED, Condition.GT, 10.0, "purchaseamount", false)
                                )
                        )
                ),
                "IS_OUTLIER"
        );

        assertNotNull(outputDataset);
        assertThat(outputDataset.count(), is(inputDataset.count()));
        outputDataset.show();
    }

    @Test
    public void testReal() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StructType schema = new StructType(new StructField[] {
                new StructField("de42_merch_id", DataTypes.StringType, false, Metadata.empty()),
                new StructField("de2_card_nbr", DataTypes.StringType, false, Metadata.empty()),
                new StructField("dw_net_pd_amt", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("dw_txn_cnt", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("dw_net_pd_cnt", DataTypes.IntegerType, false, Metadata.empty()),

        });
        Dataset<Row> dummyDataset = spark.read().schema(schema).csv(this.getClass().getResource("/dummyDataset.csv").getPath());

        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                dummyDataset,
                mapper.readValue(this.getClass().getResourceAsStream("/realFilter.json"), OutlierRemovalOptions.class).getFilters(),
                "foo"
        );

        outputDataset.show();
    }

    @Test
    public void testAggregation() {
        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                inputDataset,
                Arrays.asList(
                        new OutlierRemovalFilter(
                                Collections.singletonList("id"),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                                )
                        )
                ),
                "IS_OUTLIER"
        );

        assertNotNull(outputDataset);
        assertThat(outputDataset.count(), is(inputDataset.count()));
        outputDataset.show();
    }

    @Test
    public void testMixConfiguration() {
        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                inputDataset,
                Arrays.asList(
                        new OutlierRemovalFilter(
                                Collections.emptyList(),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.NOT_AGGREGATED, Condition.GT, 10.0, "purchaseamount", false)
                                )
                        ),
                        new OutlierRemovalFilter(
                                Collections.singletonList("id"),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                                )
                        )
                ),
                "IS_OUTLIER"
        );

        assertNotNull(outputDataset);
        assertThat(outputDataset.count(), is(inputDataset.count()));
        outputDataset.show();
    }

    @Test
    public void testMultipleAggregationSameId() {
        Dataset<Row> outputDataset = new OutlierRemoval().augmentDatasetWithOutlierCondition(
                inputDataset,
                Arrays.asList(
                        new OutlierRemovalFilter(
                                Collections.emptyList(),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.NOT_AGGREGATED, Condition.GT, 10.0, "purchaseamount", false)
                                )
                        ),
                        new OutlierRemovalFilter(
                                Collections.singletonList("id"),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                                )
                        ),
                        new OutlierRemovalFilter(
                                Collections.singletonList("id"),
                                Arrays.asList(
                                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                                )
                        )
                ),
                "IS_OUTLIER"
        );

        assertNotNull(outputDataset);
        assertThat(outputDataset.count(), is(inputDataset.count()));
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

        inputDataset = spark.read().parquet(OutlierRemovalTest.class.getResource("testKaggle/parquet").getPath());
    }

    @AfterEach
    public void tearDown() {
        if (Objects.nonNull(spark)) {
            spark.stop();
        }
    }
}