/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MondrianSparkTest {
    private SparkSession spark;
    
    @Test
    public void testMedian() {
        try (JavaSparkContext sc = new JavaSparkContext(spark.sparkContext())) {

            List<Double> values = Arrays.asList(7.0, 9.0, 13.0, 12.0, 16.0);
            JavaRDD<Double> rdd = sc.parallelize(values);

            assertEquals(12.0, MondrianSparkUtils.findMedian(rdd)._3(), 0.0001);

            values = Arrays.asList(2.7, 3.5, 5.1, 8.3);
            assertEquals(4.3, MondrianSparkUtils.findMedian(sc.parallelize(values))._3(), 0.00001);

            assertEquals(1.0, MondrianSparkUtils.findMedian(sc.parallelize(Arrays.asList(1.0)))._3(), 0.000001);
        }
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
    }

    @AfterEach
    public void tearDown() {
        if (Objects.nonNull(spark)) {
            spark.stop();
        }
    }
}
