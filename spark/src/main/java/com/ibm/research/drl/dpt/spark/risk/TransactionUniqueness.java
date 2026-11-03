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
package com.ibm.research.drl.dpt.spark.risk;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.FormatProcessor;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.processors.FormatProcessorFactory;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import com.ibm.research.drl.dpt.spark.utils.SparkEncoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class TransactionUniqueness {
    private static final Logger logger = LoggerFactory.getLogger(TransactionUniqueness.class);

    public static void main(String[] args) throws Exception {

        Options options = SparkUtils.buildCommonCommandLineOptions();
        options.addOption("remoteOutput", false, "save output remotely");
        options.addOption("basePath", true, "Base path for reading partitioned data");
        Option linkingBase = new Option("linkingBase", true, "Base path for linking tables");
        linkingBase.setRequired(false);
        options.addOption(linkingBase);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        SparkSession sparkSession = SparkUtils.createSparkSession("Data Masking");

        JsonNode configurationNode = SparkUtils.readConfigurationFile(cmd.getOptionValue("c"));
        final TransactionUniquenessOptions uniquenessOptions = new TransactionUniquenessOptions(configurationNode);

        DatasetOptions datasetOptions = uniquenessOptions.getDatasetOptions();
        DataTypeFormat inputFormat = uniquenessOptions.getInputFormat();

        DataMaskingOptions dataMaskingOptions = null;
        MaskingProviderFactory maskingProviderFactory = null;

        if (configurationNode.has("toBeMasked")) {
            dataMaskingOptions = SparkUtils.deserializeConfiguration(cmd.getOptionValue("c"), DataMaskingOptions.class);
        } else {
            dataMaskingOptions = new DataMaskingOptions(inputFormat, inputFormat, Collections.emptyMap(),
                    false, Collections.emptyMap(), datasetOptions);
        }

        if (!dataMaskingOptions.getToBeMasked().isEmpty()) {
            ConfigurationManager configurationManager = ConfigurationManager.load(
                    SparkUtils.readConfigurationFile(cmd.getOptionValue("c")));
            maskingProviderFactory = new MaskingProviderFactory(configurationManager, dataMaskingOptions.getToBeMasked());
        }

        String basePath = cmd.getOptionValue("basePath");

        logger.info("basePath: " + basePath);

        Dataset<Row> dataset = SparkUtils.createDataset(
                sparkSession,
                cmd.getOptionValue("i"),
                inputFormat,
                datasetOptions,
                basePath);


        if (uniquenessOptions.isJoinRequired()) {
            JoinInformation joinInformation = uniquenessOptions.getJoinInformation();

            logger.info("Need to join with: " + joinInformation.getRightTable());

            String joinTableLocation = cmd.getOptionValue("linkingBase", "");

            if (!joinTableLocation.isEmpty()) {
                if (!joinTableLocation.endsWith("/")) {
                    joinTableLocation += "/";
                }
            }

            joinTableLocation += joinInformation.getRightTable();

            Dataset<Row> right = SparkUtils.createDataset(
                    sparkSession,
                    joinTableLocation,
                    inputFormat,
                    datasetOptions);
            dataset = dataset.join(right, dataset.col(joinInformation.getLeftColumn()).equalTo(right.col(joinInformation.getRightColumn())));
        }

        final List<String> fieldNames = SparkUtils.createFieldNames(dataset, uniquenessOptions.getInputFormat(), uniquenessOptions.getDatasetOptions());
        final Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());
        final List<String> fieldTypes = RecordUtils.getFieldClasses(dataset.schema().fields());

        String[] idColumns = uniquenessOptions.getIdColumns();
        String[] targetColumns = uniquenessOptions.getTargetColumns();

        Map<String, Long> results = run(dataset, idColumns, targetColumns, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes,
                dataMaskingOptions, maskingProviderFactory, uniquenessOptions.getFactor(), uniquenessOptions.getThreshold());

        boolean remoteOutput = cmd.hasOption("remoteOutput");
        String outputPath = cmd.getOptionValue("o");
        try (OutputStream os = remoteOutput ? SparkUtils.createHDFSOutputStream(outputPath) : new FileOutputStream(outputPath);
             PrintStream printStream = new PrintStream(os)) {
            String output = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results);
            printStream.print(output);
        }
    }

    private static Dataset<Tuple2<String, String>> computeTransactionsToUsersWithCombinations(Dataset<Row> dataset,
                                                                                              String[] idColumns, String[] targetColumns,
                                                                                              DataTypeFormat inputFormat, DatasetOptions datasetOptions,
                                                                                              List<String> fieldNames, Map<String, Integer> fieldMap, List<String> fieldTypes,
                                                                                              DataMaskingOptions dataMaskingOptions, MaskingProviderFactory maskingProviderFactory) {

        final FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(inputFormat);

        return dataset
                .map((MapFunction<Row, Tuple2<String, String>>) row -> {
                    Record record = RecordUtils.createRecord(row, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes, false);

                    if (!dataMaskingOptions.getToBeMasked().isEmpty()) {
                        record = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);
                    }

                    final Record finalRecord = record;
                    String key = Arrays.stream(idColumns)
                            .map(col -> new String(finalRecord.getFieldValue(col)) + ":")
                            .collect(java.util.stream.Collectors.joining());
                    String value = Arrays.stream(targetColumns)
                            .map(col -> new String(finalRecord.getFieldValue(col)) + ":")
                            .collect(java.util.stream.Collectors.joining());

                    return new Tuple2<>(key, value);
                }, SparkEncoders.javaSerOf(Tuple2.class));
    }

    public static Tuple2<String, Set<String>> transactionToUID(Row row, String[] idColumns, String[] targetColumns, FormatProcessor formatProcessor,
                                                               DataTypeFormat inputFormat, DatasetOptions datasetOptions,
                                                               List<String> fieldNames, Map<String, Integer> fieldMap,
                                                               List<String> fieldTypes, DataMaskingOptions dataMaskingOptions, MaskingProviderFactory maskingProviderFactory) throws Exception {

        Record record = RecordUtils.createRecord(row, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes, false);

        for (String targetColumn : targetColumns) {
            byte[] fieldValue = record.getFieldValue(targetColumn);
            if (fieldValue == null) {
                return null;
            }
        }

        if (!dataMaskingOptions.getToBeMasked().isEmpty()) {
            record = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);
        }

        final Record finalRecord = record;
        String key = Arrays.stream(idColumns)
                .limit(idColumns.length - 1L)
                .map(col -> new String(finalRecord.getFieldValue(col)) + ":")
                .collect(java.util.stream.Collectors.joining())
                + new String(finalRecord.getFieldValue(idColumns[idColumns.length - 1]));

        String value = IntStream.range(0, targetColumns.length - 1)
                .mapToObj(i -> new String(finalRecord.getFieldValue(targetColumns[i])) + ":")
                .collect(java.util.stream.Collectors.joining())
                + new String(finalRecord.getFieldValue(targetColumns[targetColumns.length - 1]));

        return new Tuple2<>(value, new HashSet<>(Arrays.asList(key)));
    }

    private static Dataset<Tuple2<String, Set<String>>> computeTransactionsToUsersNoCombinations(Dataset<Row> dataset, String[] idColumns, String[] targetColumns,
                                                                                                 DataTypeFormat inputFormat, DatasetOptions datasetOptions,
                                                                                                 List<String> fieldNames, Map<String, Integer> fieldMap,
                                                                                                 List<String> fieldTypes, DataMaskingOptions dataMaskingOptions,
                                                                                                 MaskingProviderFactory maskingProviderFactory) {

        final FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(inputFormat);

        return dataset
                .map((MapFunction<Row, Tuple2<String, Set<String>>>) row ->
                        transactionToUID(row, idColumns, targetColumns, formatProcessor, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes, dataMaskingOptions, maskingProviderFactory),
                        SparkEncoders.javaSerOf(Tuple2.class))
                .filter((FilterFunction<Tuple2<String, Set<String>>>) Objects::nonNull)
                .groupByKey((MapFunction<Tuple2<String, Set<String>>, String>) Tuple2::_1, Encoders.STRING())
                .reduceGroups((org.apache.spark.api.java.function.ReduceFunction<Tuple2<String, Set<String>>>) (v1, v2) -> {
                    v1._2().addAll(v2._2());
                    return v1;
                })
                .map((MapFunction<Tuple2<String, Tuple2<String, Set<String>>>, Tuple2<String, Set<String>>>) kv -> kv._2(),
                        SparkEncoders.javaSerOf(Tuple2.class));
    }

    public static Map<String, Long> run(Dataset<Row> dataset,
                                        String[] idColumns, String[] targetColumns,
                                        DataTypeFormat inputFormat, DatasetOptions datasetOptions,
                                        List<String> fieldNames, Map<String, Integer> fieldMap, List<String> fieldTypes,
                                        DataMaskingOptions dataMaskingOptions, MaskingProviderFactory maskingProviderFactory,
                                        int factor, int threshold) {

        long uniqueTransactionCombinationsCount;
        long uniqueIDs;
        long totalTransactions = dataset.count();
        long totalIDs;

        if (factor > 1) {
            // group by transaction -> set of users
            Dataset<Tuple2<String, List<String>>> transactionsToUsers = computeTransactionsToUsersWithCombinations(dataset, idColumns, targetColumns, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes,
                    dataMaskingOptions, maskingProviderFactory)
                    .groupByKey((MapFunction<Tuple2<String, String>, String>) Tuple2::_2, Encoders.STRING())
                    .mapGroups((org.apache.spark.api.java.function.MapGroupsFunction<String, Tuple2<String, String>, Tuple2<String, List<String>>>) (key, iter) -> {
                        List<String> users = new ArrayList<>();
                        while (iter.hasNext()) users.add(iter.next()._1());
                        return new Tuple2<>(key, users);
                    }, SparkEncoders.javaSerOf(Tuple2.class));

            totalIDs = transactionsToUsers
                    .flatMap((FlatMapFunction<Tuple2<String, List<String>>, String>) t -> t._2().iterator(), Encoders.STRING())
                    .distinct().count();

            Dataset<Tuple2<String, List<String>>> uniqueTransactionCombinations = transactionsToUsers
                    .filter((FilterFunction<Tuple2<String, List<String>>>) r -> {
                        Set<String> s = new HashSet<>(r._2());
                        return s.size() <= threshold;
                    });

            uniqueTransactionCombinationsCount = uniqueTransactionCombinations.count();
            uniqueIDs = uniqueTransactionCombinations
                    .flatMap((FlatMapFunction<Tuple2<String, List<String>>, String>) t -> t._2().iterator(), Encoders.STRING())
                    .distinct().count();
        } else {
            Dataset<Tuple2<String, Set<String>>> transactionsToUsers = computeTransactionsToUsersNoCombinations(dataset, idColumns, targetColumns, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes,
                    dataMaskingOptions, maskingProviderFactory);

            totalIDs = transactionsToUsers
                    .flatMap((FlatMapFunction<Tuple2<String, Set<String>>, String>) t -> t._2().iterator(), Encoders.STRING())
                    .distinct().count();

            Dataset<Tuple2<String, Set<String>>> uniqueTransactionCombinations = transactionsToUsers
                    .filter((FilterFunction<Tuple2<String, Set<String>>>) r -> r._2().size() <= threshold);

            uniqueTransactionCombinationsCount = uniqueTransactionCombinations.count();
            uniqueIDs = uniqueTransactionCombinations
                    .flatMap((FlatMapFunction<Tuple2<String, Set<String>>, String>) t -> t._2().iterator(), Encoders.STRING())
                    .distinct().count();
        }

        Map<String, Long> results = new HashMap<>();
        results.put("unique_transactions", uniqueTransactionCombinationsCount);
        results.put("unique_ids", uniqueIDs);
        results.put("total_transactions", totalTransactions);
        results.put("total_ids", totalIDs);

        logger.info("Combinations leading to a unique ID: " + uniqueTransactionCombinationsCount);
        logger.info("Unique IDs : " + uniqueIDs);

        return results;
    }
}
