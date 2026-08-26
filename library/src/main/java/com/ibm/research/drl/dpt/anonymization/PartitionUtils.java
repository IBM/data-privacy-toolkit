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
package com.ibm.research.drl.dpt.anonymization;


import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Utility methods for creating and printing dataset partitions. */
public class PartitionUtils {

    /** Not instantiable. */
    private PartitionUtils() {}

    /**
     * Prints a list of fields as a single CSV record.
     *
     * @param delimiter the field delimiter character
     * @param fieldList the fields to print
     * @return the CSV-formatted string
     * @throws IOException if writing fails
     */
    public static String printToCSV(String delimiter, List<String> fieldList) throws IOException {
        try (
            StringWriter stringWriter = new StringWriter();
            CSVPrinter writer = new CSVPrinter(stringWriter, CSVFormat.RFC4180.builder().setDelimiter(delimiter.charAt(0)).setQuoteMode(QuoteMode.MINIMAL).build());
         ) {
            writer.printRecord(fieldList);
            return stringWriter.toString().trim();
        }
    }

    /**
     * Creates partitions from a dataset based on quasi-identifier column values.
     *
     * @param dataset               the dataset to partition
     * @param columnInformationList column metadata used to identify quasi-identifier columns
     * @return list of partitions
     */
    public static List<Partition> createPartitions(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        Map<String, List<List<String>>> map = new HashMap<>();

        for (int k = 0; k < dataset.getNumberOfRows(); k++) {
            List<String> row = new ArrayList<>();
            List<String> keyValues = new ArrayList<>();

            for (int j = 0; j < dataset.getNumberOfColumns(); j++) {
                String value = dataset.get(k, j);
                row.add(value);
                if (columnInformationList.get(j).getColumnType() == ColumnType.QUASI) {
                    keyValues.add(value);
                }
            }

            try {
                map.computeIfAbsent(printToCSV(",", keyValues), k2 -> new ArrayList<>()).add(row);
            } catch (Exception e) {
                throw new RuntimeException("unable to create key");
            }
        }

        List<Partition> partitions = new ArrayList<>(map.size());
        for (List<List<String>> values : map.values()) {
            partitions.add(new InMemoryPartition(values));
        }
        return partitions;
    }

    /**
     * Creates partitions from a dataset based on the specified column indices.
     *
     * @param dataset       the dataset to partition
     * @param columnIndices the indices of columns used as the partition key
     * @return list of partitions
     */
    public static List<Partition> createPartitionsByIndices(IPVDataset dataset, List<Integer> columnIndices) {
        Map<String, List<List<String>>> map = new HashMap<>();

        for (int k = 0; k < dataset.getNumberOfRows(); k++) {
            List<String> row = dataset.getRow(k);
            List<String> keyValues = new ArrayList<>();

            for (int j : columnIndices) {
                keyValues.add(dataset.get(k, j));
            }

            try {
                map.computeIfAbsent(printToCSV(",", keyValues), k2 -> new ArrayList<>()).add(row);
            } catch (Exception e) {
                throw new RuntimeException("unable to create key");
            }
        }

        List<Partition> partitions = new ArrayList<>(map.size());
        for (List<List<String>> values : map.values()) {
            partitions.add(new InMemoryPartition(values));
        }
        return partitions;
    }
}
