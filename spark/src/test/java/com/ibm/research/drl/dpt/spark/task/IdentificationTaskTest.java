package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.databind.node.TextNode;
import com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.NameIdentifier;
import com.ibm.research.drl.dpt.spark.dataset.reference.FileDatasetReference;
import com.ibm.research.drl.dpt.spark.dataset.reference.InMemoryDatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.IdentificationOptions;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentificationTaskTest {
    @Test
    public void testHappyPath() {
        InMemoryDatasetReference output = new InMemoryDatasetReference();
        InMemoryDatasetReference input = new InMemoryDatasetReference();

        IdentificationTask task = new IdentificationTask(
                "Identification",
                input,
                output,
                new IdentificationOptions(
                        "English", -1, 0.0, List.of(
                                new TextNode(EmailIdentifier.class.getCanonicalName()),
                                new TextNode(NameIdentifier.class.getCanonicalName())),
                        Collections.emptyList()
                ));

        Dataset<Row> result = task.process(task.readInputDataset(IdentificationTaskTest.class.getResource("/adult-10-30000.data.csv").getPath()));
    }
}