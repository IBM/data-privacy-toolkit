package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.spark.dataset.reference.InMemoryDatasetReference;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void simpleOneColumnMasking() throws JsonProcessingException {
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
                        Map.of("EMAIL", new DataMaskingTarget(ProviderType.EMAIL, "EMAIL")),
                        Collections.emptyMap(),
                        "",
                        JsonUtils.MAPPER.readTree("{\"_fields\": {}, \"_defaults\": {}}")
                ));

        Dataset<Row> result = task.process(input.readDataset(sparkSession, "USELESS_REFERENCE"));

        assertNotNull(result);
        assertThat(result.count(), is((long) data.size()));

        List<Row> values = result.collectAsList();

        for (Row row : values) {
            String email = row.getString(row.fieldIndex("EMAIL"));
            String name = row.getString(row.fieldIndex("Names"));

            boolean match = false;

            for (List<String> originalRow : data) {
                assertThat(email, is(not(originalRow.get(0))));

                match |= name.equals(originalRow.get(1));
            }

            assertThat(match, is(true));
        }
    }

    @Test
    public void columnSuppression() throws JsonProcessingException {
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
                        Map.of("EMAIL", new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, "EMAIL")),
                        Collections.emptyMap(),
                        "",
                        JsonUtils.MAPPER.readTree("{\"_fields\": {}, \"_defaults\": {}}")
                ));

        Dataset<Row> result = task.process(input.readDataset(sparkSession, "USELESS_REFERENCE"));

        assertNotNull(result);
        assertThat(result.count(), is((long) data.size()));

        assertThat(result.columns().length, is(1));

        assertThat("EMAIL", not(in(result.columns())));
    }

    @Test
    public void relationshipDistance() throws JsonProcessingException {
        InMemoryDatasetReference output = new InMemoryDatasetReference();

        List<String> columnNames = List.of("EMAIL", "Date1", "Date2");
        List<List<String>> data = List.of(
                List.of("jsmith@gmail.com", "2022-10-13", "2023-10-13"),
                List.of("j.doe@hotmail.com", "2022-10-13", "2023-10-13"),
                List.of("bobby@gmail.com", "2022-10-13", "2023-10-13"),
                List.of("smitty@gmail.com", "2022-10-13", "2023-10-13"),
                List.of("d.camp@gmail.com", "2022-10-13", "2023-10-13")
        );

        InMemoryDatasetReference input = new InMemoryDatasetReference(data, columnNames);

        MaskingTask task = new MaskingTask(
                "Masking",
                input,
                output,
                new MaskingOptions(
                        Map.of(
                                "Date1", new DataMaskingTarget(ProviderType.DATETIME, "Date1"),
                                "Date2", new DataMaskingTarget(ProviderType.DATETIME, "Date2")),
                        Map.of(
                                "Date1", new FieldRelationship(ValueClass.DATE, RelationshipType.DISTANCE, "Date1", new RelationshipOperand[]{
                                        new RelationshipOperand("Date2", ProviderType.DATETIME)
                                })
                        ),
                        "",
                        JsonUtils.MAPPER.readTree("{\"_fields\": {}, \"_defaults\": {" +
                                "\"datetime.format.fixed\": \"yyyy-MM-dd\"}}")
                ));

        Dataset<Row> result = task.process(input.readDataset(sparkSession, "USELESS_REFERENCE"));

        assertNotNull(result);
        assertThat(result.count(), is((long) data.size()));
    }
}