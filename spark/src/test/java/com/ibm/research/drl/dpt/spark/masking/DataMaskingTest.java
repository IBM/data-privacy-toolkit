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
package com.ibm.research.drl.dpt.spark.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ibm.research.drl.dpt.util.JsonUtils;
import com.ibm.research.drl.jsonpath.JSONPathExtractor;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRDevice;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRPatient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataMaskingTest {
    /**
     * Test mask no compound.
     */
    @Test
    public void testMaskNoCompound() {

    }

    /**
     * Test mask compound.
     */
    @Test
    public void testMaskCompound() {

    }

    @Test
    public void testMergeJSONObjectsByPath() throws Exception {

        List<Tuple2<String, String>> maskedRecords = new ArrayList<>();

        String obj1 = "{\"a\": \"valueA\", \"b\": \"foobar\", \"c\": \"valueC\"}";
        String obj2 = "{\"a\": \"foobar\", \"b\": \"valueB\", \"c\": \"valueC\"}";

        maskedRecords.add(new Tuple2<>(obj1, "/a"));
        maskedRecords.add(new Tuple2<>(obj2, "/b"));

        String finalResult = ConsistentDataMasking.mergeRecordObjectsByPath(maskedRecords, DataTypeFormat.JSON, null, null);

        JsonNode node = new ObjectMapper().readTree(finalResult);

        assertEquals("valueA", node.get("a").asText());
        assertEquals("valueB", node.get("b").asText());
        assertEquals("valueC", node.get("c").asText());
    }

    @Test
    @Disabled
    public void testExtractMissingKeyBehavior() throws Exception {
        String line = "{\"foo\": 1234}";

        JsonNode value = JSONPathExtractor.extract(line, "/location");
        String key = value.asText();
        System.out.println(">>>" + key + "<<<<");

        value = JSONPathExtractor.extract(line, "/foo");
        key = value.asText();
        System.out.println(">>>" + key + "<<<<");

        line = "{\"foo\": [1234]}";
        value = JSONPathExtractor.extract(line, "/foo");
        System.out.println(value.getClass().getName());
        key = value.asText();
        System.out.println(">>>" + key + "<<<<");

        line = "{\"foo\": null}";
        value = JSONPathExtractor.extract(line, "/foo");
        System.out.println(value.getClass().getName());
        key = value.asText();
        System.out.println(">>>" + key + "<<<<" + value.isNull());

        FHIRDevice device = new ObjectMapper().readValue(line, FHIRDevice.class);
        System.out.println(device.getLotNumber());
    }

    @Test
    @Disabled
    public void testCSVParsePerformance() throws Exception {
        int N = 1000000;


        String record = "a,b,c";
        String delimiter = ",";

        long start = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {
            try (CSVParser parser = CSVParser.parse(record, CSVFormat.RFC4180.withDelimiter(delimiter.charAt(0)));) {
                CSVRecord csvRecord = parser.getRecords().get(0);
                assertNotNull(csvRecord);
            }
        }

        long diff = System.currentTimeMillis() - start;
        System.out.println("rowToCSVString for " + N + " : " + diff);
    }

    @Test
    @Disabled
    public void testReaderPerformance() throws Exception {
        int N = 1000000;

        try (
                InputStream is = DataMaskingTest.class.getResourceAsStream("/testInputFHIR.json");
                Reader inputReader = new InputStreamReader(Objects.requireNonNull(is));
                InputStream data = DataMaskingTest.class.getResourceAsStream("/patientExample.json");
                Reader dataReader = new InputStreamReader(Objects.requireNonNull(data))) {
            String input = IOUtils.toString(inputReader);
            String patientInput = IOUtils.toString(dataReader);

            ObjectReader reader = JsonUtils.MAPPER.readerFor(FHIRDevice.class);
            ObjectReader patientReader = JsonUtils.MAPPER.readerFor(FHIRPatient.class);

            long start = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                if ((i % 2) == 0) {
                    FHIRDevice device = reader.readValue(input);
                    assertNotNull(device);
                } else {
                    FHIRPatient patient = patientReader.readValue(patientInput);
                    assertNotNull(patient);
                }
            }

            long diff = System.currentTimeMillis() - start;
            System.out.println("readValue for " + N + " : " + diff);
        }
    }

    @Test
    @Disabled
    public void testReadTreePerformance() throws Exception {
        int N = 1_000_000;

        try (InputStream is = DataMaskingTest.class.getResourceAsStream("/testInputFHIR.json");
             Reader inputReader = new InputStreamReader(Objects.requireNonNull(is))) {
            String input = IOUtils.toString(inputReader);

            long start = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                JsonNode node = JsonUtils.MAPPER.readTree(input);
                assertNotNull(node);
            }

            long diff = System.currentTimeMillis() - start;
            System.out.println("readTree for " + N + " : " + diff);
        }
    }


    @Test
    public void testConsistencyExtraction() throws IOException {
        try (
                InputStream configuration = this.getClass().getResourceAsStream("/maskingConsistencyTest.json");
                InputStream options = this.getClass().getResourceAsStream("/maskingConsistencyTest.json");
                ) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(configuration));

            final DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(options, DataMaskingOptions.class);

            Map<String, Set<String>> consistentMaskingFields = DataMasking.analyzeConfiguration(maskingOptions, configurationManager);

            assertEquals(3, consistentMaskingFields.size());
            assertEquals(3, consistentMaskingFields.get("email_ns").size());
            assertEquals(1, consistentMaskingFields.get("date_ns").size());
            assertEquals(1, consistentMaskingFields.get("name_ns").size());
        }
    }
}
