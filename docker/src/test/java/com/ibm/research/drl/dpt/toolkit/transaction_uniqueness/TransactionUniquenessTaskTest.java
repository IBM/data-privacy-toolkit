/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.transaction_uniqueness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;


public class TransactionUniquenessTaskTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void taskDeserializedCorrectly() throws Exception {
        try (InputStream conf = TransactionUniquenessTaskTest.class.getResourceAsStream("/transaction-uniqueness-ok.json")) {
            TaskToExecute uniqueness = mapper.readValue(conf, TaskToExecute.class);

            assertNotNull(uniqueness);
            assertTrue(uniqueness instanceof TransactionUniquenessTask);
        }
    }

    @Test
    public void testHappyPath() throws Exception {
        String testDataset =
                "id,date,location\n" +
                        "x,2020-10-11,loc1\n" +
                        "x,2020-10-11,loc2\n" +
                        "y,2020-10-11,loc2\n" +
                        "z,2020-10-11,loc1\n" +
                        "y,2020-10-11,loc1\n" +
                        "y,2020-10-11,loc3\n" +
                        "y,2020-10-11,loc5\n" +
                        "z,2020-10-11,loc2";

        // "id,timestamp,location,amount"

        try (
                InputStream conf = TransactionUniquenessTaskTest.class.getResourceAsStream("/transaction-uniqueness-ok.json");
                InputStream input = new ByteArrayInputStream(testDataset.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {

            TransactionUniquenessTask uniqueness = mapper.readValue(conf, TransactionUniquenessTask.class);

            uniqueness.processFile(input, output);

            System.out.println(output);

            TransactionUniquenessReport report = mapper.readValue(output.toString(), TransactionUniquenessReport.class);

            assertNotNull(report);

            assertEquals(report.getTotalIDs(), 3);
            assertEquals(report.getTotalTransactions(), 8);
            assertEquals(report.getUniqueTransactions(), 2);
            assertEquals(report.getUniqueIDs(), 1);
        }
    }
}