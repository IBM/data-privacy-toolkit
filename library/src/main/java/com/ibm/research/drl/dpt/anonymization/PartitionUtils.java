/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

public class PartitionUtils {
    public static String printToCSV(String delimiter, List<String> fieldList) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVPrinter writer = new CSVPrinter(stringWriter, CSVFormat.RFC4180.withDelimiter(delimiter.charAt(0)).withQuoteMode(QuoteMode.MINIMAL));
        writer.printRecord(fieldList);
        return stringWriter.toString().trim();
    }

    public static List<Partition> createPartitions(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        Map<String, List<List<String>>> map = new HashMap<>();

        for(int k = 0; k < dataset.getNumberOfRows(); k++) {
            List<String> row = new ArrayList<>();

            List<String> keyValues = new ArrayList<>();

            for(int j = 0; j < dataset.getNumberOfColumns(); j++) {
                String value = dataset.get(k, j);
                row.add(value);

                if (columnInformationList.get(j).getColumnType() == ColumnType.QUASI) {
                    keyValues.add(value);
                }
            }

            try {
                String key = printToCSV(",", keyValues);

                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }

                map.get(key).add(row);

            } catch (Exception e) {
                throw new RuntimeException("unable to create key");
            }
        }

        List<Partition> partitions = new ArrayList<>();
        for(List<List<String>> values: map.values()) {
            InMemoryPartition partition = new InMemoryPartition(values);
            partitions.add(partition);
        }

        return partitions;
    }

    public static List<Partition> createPartitionsByIndices(IPVDataset dataset, List<Integer> columnIndices) {
        Map<String, List<List<String>>> map = new HashMap<>();

        for(int k = 0; k < dataset.getNumberOfRows(); k++) {
            List<String> row = dataset.getRow(k); 

            List<String> keyValues = new ArrayList<>();

            for(int j: columnIndices) {
                String value = dataset.get(k, j);
                keyValues.add(value);
            }

            try {
                String key = printToCSV(",", keyValues);

                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }

                map.get(key).add(row);

            } catch (Exception e) {
                throw new RuntimeException("unable to create key");
            }
        }

        List<Partition> partitions = new ArrayList<>();
        for(List<List<String>> values: map.values()) {
            InMemoryPartition partition = new InMemoryPartition(values);
            partitions.add(partition);
        }

        return partitions;
    }
}
