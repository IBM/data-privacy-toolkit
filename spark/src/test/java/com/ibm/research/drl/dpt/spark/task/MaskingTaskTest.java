package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.databind.node.TextNode;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.NameIdentifier;
import com.ibm.research.drl.dpt.providers.masking.dicom.DAMaskingProvider;
import com.ibm.research.drl.dpt.spark.dataset.reference.InMemoryDatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.IdentificationOptions;
import com.ibm.research.drl.dpt.spark.task.option.MaskingOptions;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class MaskingTaskTest {
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
    public void testHappyPath() {
        InMemoryDatasetReference output = new InMemoryDatasetReference();

        List<String> columnNames = List.of("EMAIL", "Names");
        List<List<String>> data = List.of(
                List.of("jsmith@gmail.com", "John Smith"),
                List.of("j.doe@hotmail.com", "Jane Doe"),
                List.of("bobby@gmail.com", "Robert Tucker"),
                List.of("smitty@gmail.com", "Richard Smith"),
                List.of("d.camp@gmail.com", "Dorothy Campbell")
        );

        InMemoryDatasetReference input = new InMemoryDatasetReference(data, columnNames);

        MaskingTask task = new MaskingTask(
                "Masking",
                input,
                output,
                new MaskingOptions(
                    Map.of("EMAIL", new DAMaskingProvider())
                ));

        Dataset<Row> result = task.process(input.readDataset(sparkSession, "USELESS_REFERENCE"));

        assertNotNull(result);
        assertThat(result.count(), is((long) data.get(0).size()));
        result.show();
    }
}