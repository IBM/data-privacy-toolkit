/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
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
        final IPVDataset dataset = readInputDataset(input);
        final int threshold = getTaskOptions().getThreshold();

        Map<Integer, Set<Integer>> idsByTransaction = groupTransactionIdByTargetValues(dataset, getTaskOptions().getIdentityFields(), getTaskOptions().getExternallyObservableFields());

        List<Integer> totalIDs = countNumberOfTotalIDs(idsByTransaction);
        List<Set<Integer>> uniqueTransactionCombinations = identifyUniqueTransactions(idsByTransaction, threshold);
        List<Integer> uniqueIDs = extractIDsOfUniqueTransactions(uniqueTransactionCombinations);

        if (this.getTaskOptions().isExploreExternallyObservableFields()) {
            List<TransactionUniquenessReportColumnContribution> columnsContributions = new ArrayList<>(getTaskOptions().getExternallyObservableFields().size());
            for(String column : getTaskOptions().getExternallyObservableFields()) {
                Map<Integer, Set<Integer>> contribIdsByTransaction = groupTransactionIdByTargetValues(dataset, getTaskOptions().getIdentityFields(), Collections.singletonList((column)));
                List<Set<Integer>> contribUniqueTransactionCombinations = identifyUniqueTransactions(contribIdsByTransaction, threshold);
                List<Integer> contribUniqueIDs = extractIDsOfUniqueTransactions(contribUniqueTransactionCombinations);
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
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Set<Integer>> identifyUniqueTransactions(Map<Integer, Set<Integer>> transactionsById, int threshold) {
        return transactionsById.values().stream()
                .filter(transaction -> transaction.size() <= threshold)
                .collect(Collectors.toList());
    }

    private List<Integer> countNumberOfTotalIDs(Map<Integer, Set<Integer>> transactionsById) {
        return transactionsById.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
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
        final List<Integer> encodedIdFields = mapFieldNamesToPositions(dataset.getSchema(), identityFields);
        final List<Integer> encodedTargetFields = mapFieldNamesToPositions(dataset.getSchema(), externallyObservableFields);

        return StreamSupport.stream(dataset.spliterator(), false).
                map(row -> new Tuple<>(
                        encodeValues(row, encodedTargetFields),
                        new HashSet<>(Collections.singleton(
                                encodeValues(row, encodedIdFields)
                        ))
                ));
    }

    private Integer encodeValues(List<String> row, List<Integer> fields) {
        StringBuilder builder = new StringBuilder();
        for (Integer field : fields) {
            builder.append(row.get(field));
            builder.append('#');
        }

        return builder.toString().hashCode();
    }

    private List<Integer> mapFieldNamesToPositions(IPVSchema schema, List<String> requiredFieldNames) {
        List<? extends IPVSchemaField> schemaFields = schema.getFields();

        return requiredFieldNames.stream().map(fieldName -> {
            for (int i = 0; i < schemaFields.size(); ++i) {
                if (schemaFields.get(i).getName().equals(fieldName))
                    return i;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }).collect(Collectors.toList());
    }

    private IPVDataset readInputDataset(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            switch (getInputFormat()) {
                case CSV:
                    CSVDatasetOptions options = (CSVDatasetOptions) getInputOptions();

                    return IPVDataset.load(reader, options.isHasHeader(), options.getFieldDelimiter(), options.getQuoteChar(), options.isTrimFields());
                case JSON:
                    return JSONIPVDataset.load(reader);

                case DICOM:
                case XLS:
                case XLSX:
                case XML:
                case PDF:
                case DOC:
                case DOCX:
                case PLAIN:
                case FHIR_JSON:
                case HL7:
                case PARQUET:
                case VCF:
                case JDBC:
                default:
                    throw new IllegalArgumentException("Format not supported (at the moment). Please contact support.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Format not supported at the moment", e);
        }
    }
}

