/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.IPVAlgorithm;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.util.Counter;
import com.ibm.research.drl.dpt.util.IdentifierUtils;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FormatProcessor implements Serializable {
    private final static Logger logger = LogManager.getLogger(FormatProcessor.class);

    protected void performOutputAction(Record record, OutputStream output, Iterable<String> fieldsToSuppress) throws IOException {
        for (String field : fieldsToSuppress) {
            record.suppressField(field);
        }

        output.write(record.toString().getBytes());
        output.write(System.lineSeparator().getBytes());
    }

    public void maskStream(InputStream dataset, OutputStream output, MaskingProviderFactory factory, DataMaskingOptions dataMaskingOptions, Set<String> alreadyMaskedFields, Map<ProviderType, Class<? extends MaskingProvider>> registerTypes) throws IOException {
        logger.info("Masking stream");

        if (registerTypes != null && !registerTypes.isEmpty()) {
            for (Map.Entry<ProviderType, Class<? extends MaskingProvider>> entry : registerTypes.entrySet()) {
                ProviderType providerType = entry.getKey();
                factory.registerMaskingProviderClass(entry.getValue(), providerType);
            }
        }

        List<String> fieldsToSuppress = getFieldsToSuppress(dataMaskingOptions);

        for (Record record : extractRecords(dataset, dataMaskingOptions.getDatasetOptions())) {
            if (!record.isHeader()) {
                record = maskRecord(record, factory, alreadyMaskedFields, dataMaskingOptions);
            }

            performOutputAction(record, output, fieldsToSuppress);
        }
    }

    protected List<String> getFieldsToSuppress(DataMaskingOptions dataMaskingOptions) {
        List<String> fieldsToSuppress = new ArrayList<>();
        for (Map.Entry<String, DataMaskingTarget> toBeMasked : dataMaskingOptions.getToBeMasked().entrySet()) {
            if (toBeMasked.getValue().getProviderType().equals(ProviderType.SUPPRESS_FIELD)) {
                fieldsToSuppress.add(toBeMasked.getKey());
            }
        }
        return fieldsToSuppress;
    }

    private Map<String, OriginalMaskedValuePair> extractOperandsNotToBeMasked(Record record, Map<String, FieldRelationship> predefinedRelationships, Map<String, DataMaskingTarget> fieldsToMask) {
        final Map<String, OriginalMaskedValuePair> operandsNotToBeMasked = new HashMap<>();

        if (Objects.nonNull(predefinedRelationships)) {
            for (final FieldRelationship relationship : predefinedRelationships.values()) {
                for (final RelationshipOperand operand : relationship.getOperands()) {
                    final String operandName = operand.getName();

                    if (!fieldsToMask.containsKey(operandName)) {
                        final String operandValue = new String(record.getFieldValue(operandName));

                        operandsNotToBeMasked.put(operandName, new OriginalMaskedValuePair(operandValue, operandValue));
                    }
                }
            }
        }

        return operandsNotToBeMasked;
    }

    public Record maskRecord(Record record, MaskingProviderFactory maskingProvidersFactory, Set<String> alreadyMaskedFields, DataMaskingOptions dataMaskingOptions) {
        Map<String, FieldRelationship> relationships = dataMaskingOptions.getPredefinedRelationships();
        Map<String, DataMaskingTarget> fieldsToMask = dataMaskingOptions.getToBeMasked();
        Queue<String> queuedFields = new LinkedList<>(fieldsToMask.keySet());

        Map<String, OriginalMaskedValuePair> processedFields = extractOperandsNotToBeMasked(record, relationships, fieldsToMask);

        fieldsLoop:
        while (!queuedFields.isEmpty()) {
            String fieldIdentifier = queuedFields.poll();

            byte[] fieldValue = record.getFieldValue(fieldIdentifier);
            if (fieldValue == null) {
                processedFields.put(fieldIdentifier, new OriginalMaskedValuePair(null, null));
                continue;
            }

            String originalValue = new String(fieldValue);
            String maskedValue = originalValue;

            if (!alreadyMaskedFields.contains(fieldIdentifier)) {
                FieldRelationship fieldRelationship = null;

                if (null != relationships && !relationships.isEmpty()) {
                    fieldRelationship = relationships.get(fieldIdentifier);

                    if (fieldRelationship != null) {
                        for (RelationshipOperand operand : fieldRelationship.getOperands()) {
                            if (!processedFields.containsKey(operand.getName())) {
                                queuedFields.add(fieldIdentifier);
                                continue fieldsLoop;
                            }
                        }
                    }
                }

                ProviderType fieldType = fieldsToMask.get(fieldIdentifier).getProviderType();
                String targetPath = fieldsToMask.get(fieldIdentifier).getTargetPath();

                MaskingProvider provider = maskingProvidersFactory.get(fieldIdentifier, fieldType);

                maskedValue = maskValue(provider, originalValue, fieldIdentifier, fieldRelationship, processedFields);
                record.setFieldValue(targetPath, (maskedValue != null) ? maskedValue.getBytes() : null);
            }

            processedFields.put(fieldIdentifier, new OriginalMaskedValuePair(originalValue, maskedValue));
        }

        return record;
    }

    String maskValue(MaskingProvider provider, String originalValue, String fieldIdentifier, FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> processedFields) {
        if (null == fieldRelationship) {
            return provider.mask(originalValue, fieldIdentifier);
        } else {
            switch (fieldRelationship.getRelationshipType()) {
                case KEY:
                    return provider.maskWithKey(originalValue, processedFields.get(fieldRelationship.getOperands()[0].getName()).getOriginal());
                case DISTANCE:
                    return provider.maskDistance(
                            originalValue,
                            processedFields.get(fieldRelationship.getOperands()[0].getName()).getOriginal(),
                            processedFields.get(fieldRelationship.getOperands()[0].getName()).getMasked());
                case EQUALS:
                    return provider.maskEqual(originalValue, processedFields.get(fieldRelationship.getOperands()[0].getName()).getMasked());
                case SUM:
                case SUM_APPROXIMATE:
                case PRODUCT:
                case GREATER:
                case LESS:
                case LINKED:
                case GREP_AND_MASK:
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions) throws IOException {
        return extractRecords(dataset, dataOptions, -1);
    }

    protected abstract Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) throws IOException;

    protected void updateCounter(Map<String, Counter> columnTypes, String type) {
        Counter counter = columnTypes.computeIfAbsent(type, ignored -> new Counter(0L));

        counter.counter += 1;
    }

    protected IdentificationReport assembleReport(Map<String, Map<String, Counter>> allTypes, Map<String, Map<String, Counter>> columnTypes, long recordCount) {
        Map<String, List<IdentifiedType>> results = allTypes.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().entrySet().stream().map(valueEntry -> new IdentifiedType(
                                valueEntry.getKey(),
                                valueEntry.getValue().counter
                        )
                ).collect(Collectors.toList())
        ));

        Map<String, IdentifiedType> bestIdentifiedTypes = IdentifierUtils.getIdentifiedType(results, recordCount, IdentificationConfiguration.DEFAULT);

        for (final String fieldName : results.keySet()) {
            if (!bestIdentifiedTypes.containsKey(fieldName)) {
                bestIdentifiedTypes.put(fieldName, new IdentifiedType(ProviderType.UNKNOWN.name(), recordCount));
            }
        }

        return new IdentificationReport(
                results,
                bestIdentifiedTypes,
                recordCount
        );
    }

    protected Map<String, Map<String, Counter>> checkFieldNames(Record record, Collection<Identifier> identifiers) {
        Map<String, Map<String, Counter>> types = new HashMap<>();

        for (final String fieldReference : record.getFieldReferences()) {
            final Map<String, Counter> fieldCounters = new HashMap<>();
            final String fieldName = extractFieldName(fieldReference);

            for (Identifier identifier : identifiers) {
                if (identifier.isAppropriateName(fieldName)) {
                    if (!fieldCounters.containsKey(identifier.getType().getName())) {
                        fieldCounters.put(identifier.getType().getName(), new Counter(0L));
                    }
                    fieldCounters.get(identifier.getType().getName()).counter += 1L;
                }
            }
            if (!fieldCounters.isEmpty()) {
                types.put(fieldReference, fieldCounters);
            }
        }

        return types;
    }

    protected String extractFieldName(String fieldName) {
        return fieldName;
    }

    public IdentificationReport identifyTypesStream(InputStream input, DataTypeFormat inputFormatType,
                                                    DatasetOptions datasetOptions,
                                                    Collection<Identifier> identifiers, int firstN) throws IOException {
        Map<String, Map<String, Counter>> allTypes = new HashMap<>();
        Map<String, Map<String, Counter>> columnTypes = new HashMap<>();

        long recordCount = 0;
        for (final Record record : extractRecords(input, datasetOptions)) {
            if (record.isHeader()) {
                columnTypes = checkFieldNames(record, identifiers);

                continue;
            }

            for (final String fieldReference : record.getFieldReferences()) {
                final Map<String, Counter> fieldTypes = allTypes.computeIfAbsent(fieldReference, ignored -> new HashMap<>());

                byte[] bValue = record.getFieldValue(fieldReference);

                if (null == bValue) {
                    updateCounter(fieldTypes, ProviderType.EMPTY.getName());
                    continue;
                }

                final String value = new String(bValue);

                if (value.isEmpty()) {
                    updateCounter(fieldTypes, ProviderType.EMPTY.getName());
                    continue;
                }

                int found = 0;

                for (final Identifier identifier : identifiers) {
                    if (identifier.isOfThisType(value)) {
                        updateCounter(fieldTypes, identifier.getType().getName());
                        found += 1;
                    }
                }

                if (found == 0) {
                    updateCounter(fieldTypes, ProviderType.UNKNOWN.getName());
                }
            }

            recordCount += 1;
        }

        return assembleReport(
                allTypes,
                columnTypes,
                recordCount
        );
    }

    public boolean supportsStreams() {
        return false;
    }

    public Map<IPVVulnerability, List<Integer>> identifyVulnerabilitiesStream(InputStream input, IPVAlgorithm algorithm, DataTypeFormat inputFormatType, DatasetOptions datasetOptions, boolean isFullReport, int kValue) throws IOException {
        throw new NotImplementedException("Limited support for vulnerability identification");
    }
}
