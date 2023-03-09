/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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
                    OutputStream output = new ByteArrayOutputStream()
            ) {
                task.processFile(input, output);
            }
        }
    }
}