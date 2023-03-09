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
package com.ibm.research.drl.dpt.spark.anonymization.ola;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.ola.Lattice;
import com.ibm.research.drl.dpt.anonymization.ola.LatticeNode;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.spark.utils.DatasetUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OLASpark {

    private static JavaRDD<String> generalizeDataset(JavaRDD<String> input,
                                                     final List<ColumnInformation> columnInformationList, 
                                                     final int k, LatticeNode node, 
                                                     final char delimiter,
                                                     final char quoteChar) {
        final List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        final int[] levels = node.getValues();

        return input.mapToPair( s -> {
            try (CSVParser parser = CSVParser.parse(s, CSVFormat.RFC4180.withDelimiter(delimiter).withQuote(quoteChar));) {
                CSVRecord csvRecord = parser.getRecords().get(0);

                List<String> originalRow = new ArrayList<>();
                int numberOfTokens = csvRecord.size();

                for (int i = 0; i < numberOfTokens; i++) {
                    originalRow.add(csvRecord.get(i));
                }

                List<String> anonymizedRow = DatasetGeneralizer.generalizeRow(originalRow, columnInformationList, levels);

                StringBuilder stringBuffer = new StringBuilder();

                for (Integer quasiColumn : quasiColumns) {
                    int columnIndex = quasiColumn;
                    String value = anonymizedRow.get(columnIndex);
                    stringBuffer.append(value);
                    stringBuffer.append(":");
                }

                String key = stringBuffer.toString();

                StringWriter writer = new StringWriter();
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(delimiter).withQuoteMode(QuoteMode.MINIMAL));

                printer.printRecord(anonymizedRow);

                return new Tuple2<>(key, writer.toString().trim());
            }
        }).groupByKey().mapValues((Function<Iterable<String>, Iterable<String>>) rows -> {
            Iterator<String> iterator = rows.iterator();
            int counter = 0;

            while (iterator.hasNext()) {
                counter++;
                iterator.next();
            }

            if (counter >= k) {
                return rows;
            } else {
                List<String> suppressedRows = new ArrayList<>();

                for (String row : rows) {
                    try (
                    CSVParser parser = CSVParser.parse(row, CSVFormat.RFC4180.withDelimiter(','));
                    ) {
                        CSVRecord csvRecord = parser.getRecords().get(0);

                        int numberOfTokens = csvRecord.size();

                        List<String> values = new ArrayList<>();
                        for (int i = 0; i < numberOfTokens; i++) {
                            ColumnInformation columnInformation = columnInformationList.get(i);

                            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                                values.add("*");
                            } else {
                                String value = csvRecord.get(i);
                                values.add(value);
                            }
                        }

                        StringWriter writer = new StringWriter();
                        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(delimiter).withQuoteMode(QuoteMode.MINIMAL));

                        printer.printRecord(values);
                        suppressedRows.add(writer.toString().trim());
                    }
                }

                return suppressedRows;
            }
        }).values().flatMap(Iterable::iterator);

    }

    private static int countQuasiColumns(List<ColumnInformation> columnInformationList) {
        int counter = 0;

        for(ColumnInformation columnInformation: columnInformationList) {
            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                counter++;
            }
        }

        return counter;
    }
    
    public static JavaRDD<String> run(final InputStream configurationFileStream, JavaRDD<String> rdd) throws IOException, MisconfigurationException {
        AnonymizationOptions anonymizationOptions = new ObjectMapper().readValue(configurationFileStream, AnonymizationOptions.class);
        
        return run(anonymizationOptions, rdd);
    }
    
    public static JavaRDD<String> run(final AnonymizationOptions anonymizationOptions, JavaRDD<String> rdd) throws MisconfigurationException {

        List<ColumnInformation> columnInformationList = anonymizationOptions.getColumnInformation();
        double suppressionRate = anonymizationOptions.getSuppressionRate();
        List<PrivacyConstraint> privacyConstraints = anonymizationOptions.getPrivacyConstraints();
        
        CSVDatasetOptions csvDatasetOptions = (CSVDatasetOptions) anonymizationOptions.getDatasetOptions();
        char delimiter = csvDatasetOptions.getFieldDelimiter();
        char quoteChar = csvDatasetOptions.getQuoteChar();
        
        if (countQuasiColumns(columnInformationList) == 0) {
            throw new RuntimeException("no quasi-identifiers are present");
        }

        long startMillis = System.currentTimeMillis();

        final boolean hasHeader = DatasetUtils.checkForHeader(anonymizationOptions.getDatasetOptions());
        if (hasHeader) {
            rdd = rdd.zipWithIndex().filter( s -> (s._2() > 0)).map(Tuple2::_1);
        }
        
        int k = AnonymizationUtils.getK(privacyConstraints);

        Long inputSize = rdd.count();
        SparkAnonymityChecker sparkAnonymityChecker = new SparkAnonymityChecker(rdd, inputSize, columnInformationList, privacyConstraints, delimiter, quoteChar);
        
        Lattice lattice = new Lattice(sparkAnonymityChecker, columnInformationList, suppressionRate);
        lattice.explore();

        List<LatticeNode> kMinimalNodes = lattice.getKMinimal();
        if (kMinimalNodes == null || kMinimalNodes.size() == 0) {
            throw new RuntimeException("cannot satisfy constraints");
        }

        LatticeNode bestNode = kMinimalNodes.get(0); //TODO: link with information loss metrics

        JavaRDD<String> anonymized = generalizeDataset(rdd, columnInformationList, k, bestNode, delimiter, quoteChar);

        long diff = System.currentTimeMillis() - startMillis;

        System.out.println("==== Constraints ====");
        for(PrivacyConstraint privacyConstraint: privacyConstraints) {
            System.out.println(privacyConstraint.toString());
        }

        System.out.println(String.format("n=%d: OLA took %d milliseconds, suppression = %f (node: %s), checked %d out of %d",
                inputSize,  diff, bestNode.getSuppressionRate(), bestNode.toString(), lattice.getNodesChecked(), lattice.getTotalNodes()));


        return anonymized;
    }
}
