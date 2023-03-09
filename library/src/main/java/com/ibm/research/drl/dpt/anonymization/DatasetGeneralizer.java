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

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.commons.csv.*;

import java.io.*;
import java.util.*;

public class DatasetGeneralizer {

    private static String transformValue(String value, int level, CategoricalInformation categoricalInformation, boolean randomizeOnFail) {
        if (level == 0) {
            return value;
        }

        return categoricalInformation.getHierarchy().encode(value, level, randomizeOnFail);
    }

    private static String transformValue(String value, int level, CategoricalInformation categoricalInformation) {
        return transformValue(value, level, categoricalInformation, false);
    }

    /**
     * Generalize row list.
     *
     * @param originalRow           the original row
     * @param columnInformationList the column information list
     * @param levels                the levels
     * @return the list
     */
    public static List<String> generalizeRow(List<String> originalRow, List<ColumnInformation> columnInformationList, int[] levels) {
        int numberOfColumns = originalRow.size();

        List<String> anonymizedRow = new ArrayList<>();

        int qidIndex = 0;

        for (int j = 0; j < numberOfColumns; j++) {
            String originalValue = originalRow.get(j);

            if (columnInformationList.get(j).getColumnType() != ColumnType.QUASI) {
                anonymizedRow.add(originalValue);
            } else {
                int level = levels[qidIndex];
                CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformationList.get(j);
                String transformedValue = transformValue(originalValue, level, categoricalInformation);
                anonymizedRow.add(transformedValue);
                qidIndex++;
            }
        }

        return anonymizedRow;
    }

    public static List<String> generalizeRow(JsonNode originalRow, List<ColumnInformation> columnInformationList, int[] levels) {
        int numberOfColumns = originalRow.size();

        List<String> anonymizedRow = new ArrayList<>(numberOfColumns);

        int qidIndex = 0;

        for (int j = 0; j < numberOfColumns; j++) {
            String originalValue = originalRow.get(j).asText();

            if (columnInformationList.get(j).getColumnType() != ColumnType.QUASI) {
                anonymizedRow.add(originalValue);
            } else {
                int level = levels[qidIndex];
                CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformationList.get(j);
                anonymizedRow.add(transformValue(originalValue, level, categoricalInformation));
                qidIndex++;
            }
        }

        return anonymizedRow;
    }

    /**
     * Generalize ipv dataset.
     *
     * @param dataset               the dataset
     * @param columnInformationList the column information list
     * @param levels                the levels
     * @return the ipv dataset
     */
    public static IPVDataset generalize(IPVDataset dataset, List<ColumnInformation> columnInformationList, int[] levels) {
        IPVDataset anonymized = new IPVDataset(new ArrayList<>(), dataset.getSchema(), dataset.hasColumnNames());

        int numberOfRows = dataset.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            List<String> originalRow = dataset.getRow(i);
            List<String> anonymizedRow = generalizeRow(originalRow, columnInformationList, levels);
            anonymized.addRow(anonymizedRow);
        }

        return anonymized;
    }

    public static Collection<Partition> generalizeAndPartition(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                                               int[] levels, int contentRequirements) {
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        Map<String, Partition> partitionMap = new HashMap<>(dataset.getNumberOfRows() / 10);

        int numberOfRows = dataset.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            List<String> anonymizedRow = generalizeRow(dataset.getRow(i), columnInformationList, levels);

            String key = AnonymizationUtils.generateEQKey(anonymizedRow, quasiColumns);

            Partition p = partitionMap.get(key);
            if (p == null) {
                partitionMap.put(key, new InMemoryPartition(new IPVDataset(new ArrayList<>(), null, false)));
            }

            if (contentRequirements == ContentRequirements.NONE) {
                partitionMap.get(key).getMember().addRow(Collections.emptyList());
            } else {
                partitionMap.get(key).getMember().addRow(anonymizedRow);
            }
        }

        return partitionMap.values();
    }

    public static List<String> toList(CSVRecord record) {
        List<String> row = new ArrayList<>();

        for (int i = 0; i < record.size(); i++) {
            row.add(record.get(i));
        }

        return row;
    }

    public static Map<String, Integer> generalizeCSVAndCountEQ(InputStream dataset, List<ColumnInformation> columnInformationList, int[] levels) throws IOException {
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        Map<String, Integer> counters = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(dataset));
        String line;

        while ((line = reader.readLine()) != null) {
            CSVRecord record = CSVParser.parse(line, CSVFormat.RFC4180).getRecords().get(0);
            List<String> originalRow = toList(record);
            List<String> anonymizedRow = generalizeRow(originalRow, columnInformationList, levels);

            String key = AnonymizationUtils.generateEQKey(anonymizedRow, quasiColumns);

            counters.merge(key, 1, Integer::sum);
        }

        return counters;
    }

    public static String generalizeAndGenerateKey(List<String> originalRow,
                                                  List<Integer> quasiColumns, List<ColumnInformation> columnInformationList, int[] levels) {
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < quasiColumns.size(); i++) {
            int level = levels[i];
            int columnIndex = quasiColumns.get(i);

            CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformationList.get(columnIndex);
            String transformedValue = transformValue(originalRow.get(columnIndex), level, categoricalInformation);

            key.append(transformedValue);
            key.append(":");
        }

        return key.toString();
    }

    public static Map<String, Integer> generalizeCSVAndCountEQ(IPVDataset dataset, List<ColumnInformation> columnInformationList, int[] levels) {
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);

        int numberOfRows = dataset.getNumberOfRows();
        Map<String, Integer> counters = new HashMap<>(numberOfRows / 10);

        for (int i = 0; i < numberOfRows; i++) {
            String key = generalizeAndGenerateKey(dataset.getRow(i), quasiColumns, columnInformationList, levels);

            Integer counter = counters.get(key);

            if (counter == null) {
                counters.put(key, 1);
            } else {
                counters.put(key, counter + 1);
            }
        }

        return counters;
    }

    public static void generalizeCSVInputStream(InputStream dataset, FileWriter writer, List<ColumnInformation> columnInformationList, int[] levels) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(dataset));
        String line;

        while ((line = reader.readLine()) != null) {
            CSVRecord record = CSVParser.parse(line, CSVFormat.RFC4180).getRecords().get(0);
            List<String> originalRow = toList(record);
            List<String> anonymizedRow = generalizeRow(originalRow, columnInformationList, levels);

            StringWriter stringWriter = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.RFC4180.withDelimiter(',').withQuoteMode(QuoteMode.MINIMAL));
            csvPrinter.printRecord(anonymizedRow);
            String anonRow = stringWriter.toString().trim();

            writer.write(anonRow);
            writer.write('\n');
        }

    }
}
