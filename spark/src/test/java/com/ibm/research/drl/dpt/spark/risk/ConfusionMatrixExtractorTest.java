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
package com.ibm.research.drl.dpt.spark.risk;

import com.ibm.research.drl.dpt.util.JsonUtils;
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


    @Test
    public void testNotAggregated() throws IOException {
        OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_not_aggregated.json"), OutlierRemovalOptions.class);
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
        OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_one_aggregated.json"), OutlierRemovalOptions.class);

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
            OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(inputStream, OutlierRemovalOptions.class);

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
        OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_two_aggregated_different_id.json"), OutlierRemovalOptions.class);

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
        OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_aggregated_with_not_aggregated.json"), OutlierRemovalOptions.class);

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
        OutlierRemovalOptions configuration = JsonUtils.MAPPER.readValue(ConfusionMatrixExtractorTest.class.getResourceAsStream("/filter_one_aggregated_two_fields.json"), OutlierRemovalOptions.class);

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