/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled("not fully functional yet, and I'm not sure this is the best way to test Spark")
public class IdentificationSparkTest {
    private SparkContext sc;

    @BeforeEach
    public void setUp() throws Exception {
        sc = new SparkContext(new SparkConf(true).
                setMaster("local[*]").
                setAppName("testingIdentification").set("spark.driver.host", "localhost"));
    }

    @BeforeEach
    public void tearDown() throws Exception {
        sc.stop();
    }

    @Test
    public void run() throws Exception {

        /*
        IdentificationSpark.identifyDataset(
                sc.textFile(this.getClass().getResource("/adult-10-30000.data.csv").getPath(), 1).toJavaRDD(),
                false,
                ",",
                '"');
                */
    }

}