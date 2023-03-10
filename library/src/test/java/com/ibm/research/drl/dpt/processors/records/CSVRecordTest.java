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
package com.ibm.research.drl.dpt.processors.records;


import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVRecordTest {
    
    @Test
    public void testFromString() throws Exception {
        String input = "a,b,c";

        DatasetOptions datasetOptions = new CSVDatasetOptions(false, ',', '"', false);
        Map<String, Integer> fieldNames = new HashMap<>();
        fieldNames.put("first", 0);
        fieldNames.put("second", 1);
        fieldNames.put("third", 2);
        
        Record record = CSVRecord.fromString(input, datasetOptions, fieldNames, false);
        
        assertEquals("a", new String(record.getFieldValue("first")));
        assertEquals("b", new String(record.getFieldValue("second")));
        assertEquals("c", new String(record.getFieldValue("third")));
        
    }

    @Test
    public void testInvalidFieldName() throws Exception {
        assertThrows(NullPointerException.class, () -> {
            String input = "a,b,c";

            DatasetOptions datasetOptions = new CSVDatasetOptions(false, ',', '"', false);
            Map<String, Integer> fieldNames = new HashMap<>();
            fieldNames.put("first", 0);
            fieldNames.put("second", 1);
            fieldNames.put("third", 2);

            Record record = CSVRecord.fromString(input, datasetOptions, fieldNames, false);

            System.out.println(new String(record.getFieldValue("nonexistent")));
        });
    }

    @Test
    public void testFromStringRespectsQuotes() throws Exception {
        String input = "a,\"b1,b2\",c";

        DatasetOptions datasetOptions = new CSVDatasetOptions(false, ',', '"', false);
        Map<String, Integer> fieldNames = new HashMap<>();
        fieldNames.put("first", 0);
        fieldNames.put("second", 1);
        fieldNames.put("third", 2);

        Record record = CSVRecord.fromString(input, datasetOptions, fieldNames, false);

        assertEquals("a", new String(record.getFieldValue("first")));
        assertEquals("b1,b2", new String(record.getFieldValue("second")));
        assertEquals("c", new String(record.getFieldValue("third")));
    }
    
    @Test
    public void testToString() {
        
        String[] data =  new String[]{"a", "b", "c"};
        
        Map<String, Integer> fieldNames = new HashMap<>();
        fieldNames.put("first", 0);
        fieldNames.put("second", 1);
        fieldNames.put("third", 2);
        
        CSVRecord record = new CSVRecord(data, fieldNames, new CSVDatasetOptions(false, ',', '"', false), false);
        
        assertEquals("a,b,c", record.toString());
        
    }

    @Test
    void testSuppressFieldRemovesHeaderAndValues() throws IOException {
        String input = "a,b,c";

        DatasetOptions datasetOptions = new CSVDatasetOptions(false, ',', '"', false);
        Map<String, Integer> fieldNames = new HashMap<>();
        fieldNames.put("first", 0);
        fieldNames.put("second", 1);
        fieldNames.put("third", 2);

        Record record = CSVRecord.fromString(input, datasetOptions, fieldNames, false);
        record.suppressField("second");

        Set<String> fields = (Set<String>) record.getFieldReferences();

        assertThat(fields.contains("first"), is(true));
        assertThat(fields.contains("second"), is(false));
        assertThat(fields.contains("third"), is(true));

        assertThat(new String(record.getFieldValue("first")), is("a"));
        assertThat(new String(record.getFieldValue("third")), is("c"));
    }
}
