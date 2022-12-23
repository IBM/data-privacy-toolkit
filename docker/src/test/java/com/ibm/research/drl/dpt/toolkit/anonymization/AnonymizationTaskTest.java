/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AnonymizationTaskTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testAnonymizationTaskHappyPath() throws Exception {
        try (InputStream inputStream = AnonymizationTaskTest.class.getResourceAsStream("/configuration_test_anonymization.json")) {
            AnonymizationTask task = mapper.readValue(inputStream, AnonymizationTask.class);

            assertNotNull(task);

            try (
                    InputStream input = AnonymizationTaskTest.class.getResourceAsStream("/healthcare-dataset.txt");
                    OutputStream output = new ByteArrayOutputStream();
            ) {
                task.processFile(input, output);
            }
        }
    }
}