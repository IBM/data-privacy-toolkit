/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.configuration.IdentificationStrategy;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class IdentificationExecutorTest {
    private static SparkSession spark;
    private static Dataset<Row> adultDataset;

    @BeforeAll
    public static void setUp() {
        LogManager.getLogger(IdentificationExecutor.class).setLevel(Level.ALL);
        spark = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true).
                set("spark.io.compression.codec", "snappy").
                setMaster("local[*]").
                setAppName("IdentificationExecutorTest").set("spark.driver.host", "localhost"))).getOrCreate();

        DataFrameReader reader = spark.read()
                .option("sep", ",")
                .option("quote", "\"")
                .option("header", false);

        adultDataset = reader.csv(IdentificationExecutorTest.class.getResource("/adult-10-30000.data.csv").getPath());
    }

    @AfterAll
    public static void tearDown() {
        spark.stop();
    }

    @Test
    public void doSelectingShouldSelect() {
        IdentificationExecutor identificationExecutor = new IdentificationExecutor();

        IdentificationOptions identificationOptions = new IdentificationOptions(
                false,
                0,
                false,
                false,
                true,
                0.5,
                null,
                null,
                "_c9",
                null,
                false
        );

        Dataset<Row> selectedDataset = identificationExecutor.doSelecting(adultDataset, identificationOptions);
        assertThat(selectedDataset, is(not(nullValue())));

        int selectedColumns = selectedDataset.columns().length;
        int expectedColumns = 1;
        assertThat(selectedColumns, is(expectedColumns));

        String selectedColumn = selectedDataset.columns()[0];
        assertThat(selectedColumn, is(identificationOptions.getSpecificField()));
    }

    @Test
    public void doSamplingShouldSample() {
        IdentificationExecutor identificationExecutor = new IdentificationExecutor();

        IdentificationOptions identificationOptions = new IdentificationOptions(
                false,
                0,
                false,
                false,
                true,
                0.5,
                null,
                null,
                null,
                null,
                false
        );

        Dataset<Row> sampledDataset = identificationExecutor.doSampling(adultDataset, identificationOptions);
        assertThat(sampledDataset, is(not(nullValue())));

        double originalRows = adultDataset.count();
        double sampledRows = sampledDataset.count();
        double expectedSamples = originalRows * identificationOptions.getSampleFraction();
        double samplesVariance = identificationOptions.getSampleFraction() * (1 - identificationOptions.getSampleFraction()) * originalRows; // delta needed here because Spark uses a Bernoulli sampler by default, so let's use its variance
        assertThat(sampledRows, is(closeTo(expectedSamples, samplesVariance)));
    }

    @Test
    public void doLimitingShouldLimit() {
        IdentificationExecutor identificationExecutor = new IdentificationExecutor();

        IdentificationOptions identificationOptions = new IdentificationOptions(
                true,
                10,
                false,
                false,
                false,
                0,
                null,
                null,
                null,
                null,
                false
        );

        Dataset<Row> limitedDataset = identificationExecutor.doLimiting(adultDataset, identificationOptions);
        assertThat(limitedDataset, is(not(nullValue())));

        double limitedRows = limitedDataset.count();
        double expectedRows = identificationOptions.getN();
        assertThat(limitedRows, is(expectedRows));
    }

    @Test
    public void identifyShouldIdentifyCorrectly() {
        IdentificationExecutor identificationExecutor = new IdentificationExecutor();

        IdentificationOptions identificationOptions = new IdentificationOptions(
                false,
                0,
                false,
                false,
                false,
                0,
                null,
                null,
                null,
                null,
                false
        );
        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(
                50,
                20,
                false,
                Collections.emptyMap(),
                Collections.emptyMap(),
                IdentificationStrategy.FREQUENCY_BASED
        );

        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode confJson = objectMapper.valueToTree(identificationOptions);

        IdentificationResults identifiedFields = identificationExecutor.identifyDataset(
                adultDataset,
                confJson,
                identificationOptions,
                identificationConfiguration
        );
        assertThat(identifiedFields, is(not(nullValue())));

        Map<String, List<IdentifiedType>> bestTypes = identifiedFields.getBestTypes();
        assertThat(bestTypes, is(not(nullValue())));

        List<IdentifiedType> _c0 = bestTypes.get("_c0");
        assertThat(_c0, is(not(nullValue())));
        assertThat(_c0.size(), is(0));

        List<IdentifiedType> _c1 = bestTypes.get("_c1");
        assertThat(_c1, is(not(nullValue())));
        assertThat(_c1.size(), is(0));

        List<IdentifiedType> _c2 = bestTypes.get("_c2");
        assertThat(_c2, is(not(nullValue())));
        assertThat(_c2.size(), is(0));

        List<IdentifiedType> _c3 = bestTypes.get("_c3");
        assertThat(_c3, is(not(nullValue())));
        assertThat(_c3.size(), is(0));

        List<IdentifiedType> _c4 = bestTypes.get("_c4");
        assertThat(_c4, is(not(nullValue())));
        assertThat(_c4.size(), is(0));

        List<IdentifiedType> _c5 = bestTypes.get("_c5");
        assertThat(_c5, is(not(nullValue())));
        assertThat(_c5.size(), is(1));
        assertThat(_c5.get(0).getTypeName(), is(ProviderType.MARITAL_STATUS.getName()));
        assertThat(_c5.get(0).getCount(), is(30000L));

        List<IdentifiedType> _c6 = bestTypes.get("_c6");
        assertThat(_c6, is(not(nullValue())));
        assertThat(_c6.size(), is(0));

        List<IdentifiedType> _c7 = bestTypes.get("_c7");
        assertThat(_c7, is(not(nullValue())));
        assertThat(_c7.size(), is(1));
        assertThat(_c7.get(0).getTypeName(), is(ProviderType.DEPENDENT.getName()));
        assertThat(_c7.get(0).getCount(), is(13580L));

        List<IdentifiedType> _c8 = bestTypes.get("_c8");
        assertThat(_c8, is(not(nullValue())));
        assertThat(_c8.size(), is(1));
        assertThat(_c8.get(0).getTypeName(), is(ProviderType.RACE.getName()));
        assertThat(_c8.get(0).getCount(), is(28509L));

        List<IdentifiedType> _c9 = bestTypes.get("_c9");
        assertThat(_c9, is(not(nullValue())));
        assertThat(_c9.size(), is(1));
        assertThat(_c9.get(0).getTypeName(), is(ProviderType.GENDER.getName()));
        assertThat(_c9.get(0).getCount(), is(30000L));
    }

    @Test
    public void initTest() throws IOException, ParseException {
        IdentificationExecutor identificationExecutor = new IdentificationExecutor();

        String[] args = new String[] {
                "-i",
                "scripts/test_input_with_header.csv.json",
                "-o",
                "scripts/test_output_report.json",
                "-c",
                "scripts/identification_conf.json"
        };

        identificationExecutor.init(args);

        assertThat(identificationExecutor.identificationOptions, is(not(nullValue())));
        assertThat(identificationExecutor.identificationConfiguration, is(not(nullValue())));
    }
}