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
package com.ibm.research.drl.dpt.toolkit.transaction_uniqueness;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.toolkit.dataset.GenericDatasetOptions;
import com.ibm.research.drl.dpt.toolkit.dataset.JSONIPVDataset;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TransactionUniquenessTask extends TaskToExecute {
    private static final Logger logger = LogManager.getLogger(TransactionUniquenessTask.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final TransactionUniquenessOptions taskOptions;

    @JsonCreator
    public TransactionUniquenessTask(
            @JsonProperty("task") String task,
            @JsonProperty("extension") String extension,
            @JsonProperty("inputFormat") DataTypeFormat inputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "inputFormat",
                    defaultImpl = GenericDatasetOptions.class
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("inputOptions") DatasetOptions inputOptions,
            @JsonProperty("taskOptions") TransactionUniquenessOptions taskOptions,
            @JsonProperty("outputFormat") DataTypeFormat outputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "outputFormat",
                    defaultImpl = GenericDatasetOptions.class
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("outputOptions") DatasetOptions outputOptions
    ) {
        super(task, extension, inputFormat, inputOptions, outputFormat, outputOptions);

        this.taskOptions = taskOptions;
    }

    @Override
    public TransactionUniquenessOptions getTaskOptions() {
        return this.taskOptions;
    }

    @Override
    public String buildOutputExtension() {
        return "json";
    }

    @Override
    public void processFile(InputStream input, OutputStream output) throws MisconfigurationException, IOException {
        final var dataset = readInputDataset(input);
        final int threshold = getTaskOptions().getThreshold();

        var idsByTransaction = groupTransactionIdByTargetValues(dataset, getTaskOptions().getIdentityFields(), getTaskOptions().getExternallyObservableFields());

        var totalIDs = countNumberOfTotalIDs(idsByTransaction);
        var uniqueTransactionCombinations = identifyUniqueTransactions(idsByTransaction, threshold);
        var uniqueIDs = extractIDsOfUniqueTransactions(uniqueTransactionCombinations);

        if (this.getTaskOptions().isExploreExternallyObservableFields()) {
            var columnsContributions = new ArrayList<TransactionUniquenessReportColumnContribution>(getTaskOptions().getExternallyObservableFields().size());
            for (String column : getTaskOptions().getExternallyObservableFields()) {
                var contribIdsByTransaction = groupTransactionIdByTargetValues(dataset, getTaskOptions().getIdentityFields(), Collections.singletonList(column));
                var contribUniqueTransactionCombinations = identifyUniqueTransactions(contribIdsByTransaction, threshold);
                var contribUniqueIDs = extractIDsOfUniqueTransactions(contribUniqueTransactionCombinations);
                columnsContributions.add(new TransactionUniquenessReportColumnContribution(
                        column,
                        contribUniqueTransactionCombinations.size(),
                        contribUniqueIDs.size()
                ));
            }
            mapper.writeValue(output, new TransactionUniquenessReport(
                    totalIDs.size(),
                    dataset.getNumberOfRows(),
                    uniqueTransactionCombinations.size(),
                    uniqueIDs.size(),
                    columnsContributions
            ));
        } else {
            mapper.writeValue(output, new TransactionUniquenessReport(
                    totalIDs.size(),
                    dataset.getNumberOfRows(),
                    uniqueTransactionCombinations.size(),
                    uniqueIDs.size(),
                    null
            ));
        }
    }

    private List<Integer> extractIDsOfUniqueTransactions(List<Set<Integer>> uniqueTransactionCombinations) {
        return uniqueTransactionCombinations.stream()
                .flatMap(Set::stream)
                .distinct()
                .toList();
    }

    private List<Set<Integer>> identifyUniqueTransactions(Map<Integer, Set<Integer>> transactionsById, int threshold) {
        return transactionsById.values().stream()
                .filter(transaction -> transaction.size() <= threshold)
                .toList();
    }

    private List<Integer> countNumberOfTotalIDs(Map<Integer, Set<Integer>> transactionsById) {
        return transactionsById.values().stream()
                .flatMap(Set::stream)
                .distinct()
                .toList();
    }

    private Map<Integer, Set<Integer>> groupTransactionIdByTargetValues(IPVDataset dataset, List<String> identityFields, List<String> externallyObservableFields) {
        Stream<Tuple<Integer, Set<Integer>>> encodedTransactions = encodeTransactionAndId(dataset, identityFields, externallyObservableFields);

        Map<Integer, Set<Integer>> groupedTransactionIDs = new HashMap<>();

        encodedTransactions.forEach(
                transaction -> groupedTransactionIDs.merge(
                        transaction.getFirst(),
                        transaction.getSecond(),
                        (set1, set2) -> {
                            set1.addAll(set2);
                            return set1;
                        })
        );

        return groupedTransactionIDs;
    }

    private Stream<Tuple<Integer, Set<Integer>>> encodeTransactionAndId(IPVDataset dataset, List<String> identityFields, List<String> externallyObservableFields) {
        final var encodedIdFields = mapFieldNamesToPositions(dataset.getSchema(), identityFields);
        final var encodedTargetFields = mapFieldNamesToPositions(dataset.getSchema(), externallyObservableFields);

        return StreamSupport.stream(dataset.spliterator(), false)
                .map(row -> new Tuple<>(
                        encodeValues(row, encodedTargetFields),
                        new HashSet<>(Collections.singleton(encodeValues(row, encodedIdFields)))
                ));
    }

    private Integer encodeValues(List<String> row, List<Integer> fields) {
        return fields.stream()
                .map(row::get)
                .collect(Collectors.joining("#"))
                .hashCode();
    }

    private List<Integer> mapFieldNamesToPositions(IPVSchema schema, List<String> requiredFieldNames) {
        final var schemaFields = schema.getFields();

        return requiredFieldNames.stream()
                .map(fieldName -> IntStream.range(0, schemaFields.size())
                        .filter(i -> schemaFields.get(i).getName().equals(fieldName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unknown field " + fieldName))
                )
                .toList();
    }

    private IPVDataset readInputDataset(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return switch (getInputFormat()) {
                case CSV -> {
                    var options = (CSVDatasetOptions) getInputOptions();
                    yield IPVDataset.load(reader, options.isHasHeader(), options.getFieldDelimiter(), options.getQuoteChar(), options.isTrimFields());
                }
                case JSON -> JSONIPVDataset.load(reader);
                default -> throw new IllegalArgumentException("Format not supported (at the moment). Please contact support.");
            };
        } catch (IOException e) {
            throw new RuntimeException("Format not supported at the moment", e);
        }
    }
}
