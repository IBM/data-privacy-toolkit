/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.to_date;
import static org.apache.spark.sql.functions.to_timestamp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CsvToParquetConverterTest {
    private SparkSession spark;

    @BeforeEach
    public void setUp() {
        spark = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true).
                setMaster("local").
                setAppName("CsvToParquetConverterTest").set("spark.driver.host", "localhost"))).getOrCreate();
    }

    @AfterEach
    public void tearDown() {
        if (null != spark) {
            spark.stop();
        }
    }
   
    @Test
    @Disabled("Utility test")
    public void testBinary() {
        String s = "\u0001";
        System.out.println(s.length());
        s = "\\u0001";
        String t = StringEscapeUtils.unescapeJava(s);
    }
    
    @Test
    public void testExtractDecimalType() {
        String s = "decimal";
        DecimalType defaultDecimal = new DecimalType();
        
        DecimalType parsed = (DecimalType) CsvToParquetConverter.extractDecimalType(s);
        assertEquals(parsed.precision(), defaultDecimal.precision());
        assertEquals(parsed.scale(), defaultDecimal.scale());
        
        s = "numeric(21)";
        parsed = (DecimalType) CsvToParquetConverter.extractDecimalType(s);
        assertEquals(21, parsed.precision());
        assertEquals(0, parsed.scale());

        s = "numeric(21,3)";
        parsed = (DecimalType) CsvToParquetConverter.extractDecimalType(s);
        assertEquals(21, parsed.precision());
        assertEquals(3, parsed.scale());

        s = "numeric(21, 3)";
        parsed = (DecimalType) CsvToParquetConverter.extractDecimalType(s);
        assertEquals(21, parsed.precision());
        assertEquals(3, parsed.scale());
        
    }

    @Test
    public void testNullDates() {
        StructType schema = new StructType(new StructField[]{
                new StructField("A", DataTypes.StringType, true, Metadata.empty())
        });

        Dataset<Row> dataset = spark.createDataset(
                Arrays.asList(
                        RowFactory.create(
                                "2016-01-29"
                        ),
                        RowFactory.create(
                                "2016-01-29"
                        ),
                        RowFactory.create(
                                (String) null
                        ),
                        RowFactory.create(
                                "2016-01-29"
                        ),
                        RowFactory.create(
                                "2016-01-29"
                        )
                        ),
                RowEncoder.apply(schema));

        Dataset<Row> after = dataset.select(to_date(col("A")).as("A"));

        assertNotNull(after);
        assertThat(after.filter(col("A").isNull()).count(), is(1L));
    }

    @Test
    public void testMultipleFormatsWithConversion() {
        StructType schema = new StructType(new StructField[]{
                new StructField("A", DataTypes.StringType, false, Metadata.empty()),
                new StructField("B", DataTypes.StringType, false, Metadata.empty())
        });

        Dataset<Row> dataset = spark.createDataset(
                Arrays.asList(
                        RowFactory.create(
                                "2016-01-29 00:00",
                                "10/09/1098 12:11:12"
                        ),
                        RowFactory.create(
                                "2016-01-29 00:00",
                                "10/09/1098 12:11:12"
                        ),
                        RowFactory.create(
                                "2016-01-29 00:00",
                                "10/09/1098 12:11:12"
                        ),
                        RowFactory.create(
                                "2016-01-29 00:00",
                                "10/09/1098 12:11:12"
                        )
                ),
                RowEncoder.apply(schema));

        Dataset<Row> after = dataset.select(
                to_timestamp(col("A"), "yyyy-MM-dd HH:mm").as("A"),
                to_timestamp(col("B"), "dd/MM/yyyy HH:mm:ss").as("B"));

        assertNotNull(after);
        assertNotEquals(0, after.filter(col("A").isNotNull().and(col("B").isNotNull())).count());
    }

    @Test
    public void testExtractTimestampFormats() throws Exception {
        try (InputStream input =  CsvToParquetConverterTest.class.getResourceAsStream("/test_ts.yaml")) {
            JsonNode configuration = new ObjectMapper(new YAMLFactory()).readTree(input);

            Map<String, String> timestampFormats = CsvToParquetConverter.getTimestampFormats(configuration);

            assertEquals(3, timestampFormats.size());
            assertEquals("dd/MM/yyyy HH:mm:ss", timestampFormats.get("WAREHOUSE_APPLIED_DATE"));
            assertEquals("dd/MM/yyyy HH:mm", timestampFormats.get("HIERARCHY_DATE"));
            assertNull(timestampFormats.get("DAY"));

            assertEquals(3, CsvToParquetConverter.countUniqueTimestampFormats(timestampFormats));
        }
    }
    
    @Test
    @Disabled("Utility testing to debug timezone issues")
    public void testTSConversion() throws Exception {
       String input = "2017-03-12 02:00:00";
       String timezone = "America/Chicago";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        Date parsedDate = dateFormat.parse(input);

        System.out.println(parsedDate.toString());
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        System.out.println(timestamp);
        
        Timestamp ts2 = Timestamp.valueOf(input);
        System.out.println(ts2);
    }
}