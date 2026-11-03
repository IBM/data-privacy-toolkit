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
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import com.ibm.research.drl.dpt.spark.utils.SparkEncoders;
import scala.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OLASpark {

    private static Dataset<String> generalizeDataset(Dataset<String> input,
                                                     final List<ColumnInformation> columnInformationList,
                                                     final int k, LatticeNode node,
                                                     final char delimiter,
                                                     final char quoteChar) {
        final List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        final int[] levels = node.getValues();

        // Map each row to (key, csvRow) pair, group by key, then suppress or keep
        return input
                .map((MapFunction<String, Tuple2<String, String>>) s -> {
                    try (CSVParser parser = CSVParser.parse(s, CSVFormat.RFC4180.withDelimiter(delimiter).withQuote(quoteChar))) {
                        CSVRecord csvRecord = parser.getRecords().get(0);

                        List<String> originalRow = new ArrayList<>();
                        int numberOfTokens = csvRecord.size();
                        for (int i = 0; i < numberOfTokens; i++) {
                            originalRow.add(csvRecord.get(i));
                        }

                        List<String> anonymizedRow = DatasetGeneralizer.generalizeRow(originalRow, columnInformationList, levels);

                        StringBuilder stringBuffer = new StringBuilder();
                        for (Integer quasiColumn : quasiColumns) {
                            stringBuffer.append(anonymizedRow.get(quasiColumn)).append(":");
                        }
                        String key = stringBuffer.toString();

                        StringWriter writer = new StringWriter();
                        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(delimiter).withQuoteMode(QuoteMode.MINIMAL));
                        printer.printRecord(anonymizedRow);

                        return new Tuple2<>(key, writer.toString().trim());
                    }
                }, SparkEncoders.javaSerOf(Tuple2.class))
                .groupByKey((MapFunction<Tuple2<String, String>, String>) Tuple2::_1, Encoders.STRING())
                .flatMapGroups((org.apache.spark.api.java.function.FlatMapGroupsFunction<String, Tuple2<String, String>, String>) (key, rowsIter) -> {
                    List<Tuple2<String, String>> rows = new ArrayList<>();
                    while (rowsIter.hasNext()) rows.add(rowsIter.next());

                    if (rows.size() >= k) {
                        return rows.stream().map(Tuple2::_2).iterator();
                    } else {
                        List<String> suppressedRows = new ArrayList<>();
                        for (Tuple2<String, String> r : rows) {
                            try (CSVParser parser = CSVParser.parse(r._2(), CSVFormat.RFC4180.withDelimiter(','))) {
                                CSVRecord csvRecord = parser.getRecords().get(0);
                                int numberOfTokens = csvRecord.size();
                                List<String> values = new ArrayList<>();
                                for (int i = 0; i < numberOfTokens; i++) {
                                    ColumnInformation columnInformation = columnInformationList.get(i);
                                    if (columnInformation.getColumnType() == ColumnType.QUASI) {
                                        values.add("*");
                                    } else {
                                        values.add(csvRecord.get(i));
                                    }
                                }
                                StringWriter writer = new StringWriter();
                                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(delimiter).withQuoteMode(QuoteMode.MINIMAL));
                                printer.printRecord(values);
                                suppressedRows.add(writer.toString().trim());
                            }
                        }
                        return suppressedRows.iterator();
                    }
                }, Encoders.STRING());
    }

    private static int countQuasiColumns(List<ColumnInformation> columnInformationList) {
        int counter = 0;
        for (ColumnInformation columnInformation : columnInformationList) {
            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                counter++;
            }
        }
        return counter;
    }

    public static Dataset<String> run(final InputStream configurationFileStream, Dataset<String> rdd) throws IOException, MisconfigurationException {
        AnonymizationOptions anonymizationOptions = new ObjectMapper().readValue(configurationFileStream, AnonymizationOptions.class);
        return run(anonymizationOptions, rdd);
    }

    public static Dataset<String> run(final AnonymizationOptions anonymizationOptions, Dataset<String> rdd) throws MisconfigurationException {

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
            final Dataset<String> withIndex = rdd.withColumn("__idx__",
                    org.apache.spark.sql.functions.monotonically_increasing_id())
                    .as(Encoders.STRING());
            // Simpler: just filter out the first row via zipWithIndex approach using Dataset
            // Use a flag approach: collect first, skip first line
            rdd = rdd.filter((FilterFunction<String>) s -> true); // placeholder - handled below
            // Actually filter header by converting to dataset with index
            rdd = rdd.sparkSession().createDataset(
                    rdd.toJavaRDD().zipWithIndex()
                            .filter(t -> t._2() > 0)
                            .map(Tuple2::_1)
                            .rdd(),
                    Encoders.STRING());
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

        LatticeNode bestNode = kMinimalNodes.get(0);

        Dataset<String> anonymized = generalizeDataset(rdd, columnInformationList, k, bestNode, delimiter, quoteChar);

        long diff = System.currentTimeMillis() - startMillis;

        System.out.println("==== Constraints ====");
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            System.out.println(privacyConstraint.toString());
        }

        System.out.printf("n=%d: OLA took %d milliseconds, suppression = %f (node: %s), checked %d out of %d%n",
                inputSize, diff, bestNode.getSuppressionRate(), bestNode.toString(), lattice.getNodesChecked(), lattice.getTotalNodes());

        return anonymized;
    }
}
