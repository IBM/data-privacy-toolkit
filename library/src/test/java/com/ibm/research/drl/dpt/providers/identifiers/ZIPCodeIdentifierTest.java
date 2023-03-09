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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZIPCodeIdentifierTest {

    @Test
    public void testZIPCodeIdentifier() {
        Identifier identifier = new ZIPCodeIdentifier();

        assertTrue(identifier.isOfThisType("01950"));
        assertFalse(identifier.isOfThisType("001950"));
        assertFalse(identifier.isOfThisType("0195000"));
        assertFalse(identifier.isOfThisType("000"));
    }

    @Test
    public void testAgainstKnownDataset() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/healthcare-dataset.csv")) {
            IPVDataset dataset = IPVDataset.load(inputStream, true, ',', '"', false);

            Identifier identifier = new ZIPCodeIdentifier();
            int idx = dataset.getSchema().getFields().stream().map(IPVSchemaField::getName).collect(Collectors.toList()).indexOf("ZIP");

            for (List<String> row: dataset) {
                if (!identifier.isOfThisType(row.get(idx))) {
                    System.out.println(row.get(idx));
                }
            }
        }
    }
}
