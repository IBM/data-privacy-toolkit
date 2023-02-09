package com.ibm.research.drl.dpt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DPTSparkDriverTest {
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
    @Disabled("To be enabled once fixed")
    public void testMain() throws IOException {
        DPTSparkDriver.main(new String[] {
                "-i",
                DPTSparkDriver.class.getResource("/transactions.csv").getPath(),
                "-c",
                DPTSparkDriver.class.getResource("/transaction-uniqueness-ok.json").getPath(),
                "-o",
                Files.createTempFile("transaction-uniqueness", "report").toAbsolutePath().toString()});
    }
}