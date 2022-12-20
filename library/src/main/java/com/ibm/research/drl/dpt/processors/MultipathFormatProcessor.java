/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.processors.records.MultipathRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.util.Counter;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public abstract class MultipathFormatProcessor extends FormatProcessor {
    private final static Logger logger = LogManager.getLogger(MultipathFormatProcessor.class);

    protected Map<String, OriginalMaskedValuePair> extractOperandsNotToBeMasked(MultipathRecord record, Map<String, FieldRelationship> predefinedRelationships,
                                                                                Map<String, Tuple<DataMaskingTarget, String>> fieldsToMask) {

        final Map<String, OriginalMaskedValuePair> operandsNotToBeMasked = new HashMap<>();

        if (Objects.nonNull(predefinedRelationships)) {
            for (final FieldRelationship relationship : predefinedRelationships.values()) {
                final String fieldName = relationship.getFieldName();

                for (final RelationshipOperand operand : relationship.getOperands()) {
                    final String operandName = operand.getName();

                    if (record.isAbsolute(operandName)) {
                        if (!fieldsToMask.containsKey(operandName)) {
                            final String operandValue = new String(record.getFieldValue(operandName));

                            operandsNotToBeMasked.put(operandName, new OriginalMaskedValuePair(operandValue, operandValue));
                        }
                    } else if (record.isSingleElement(fieldName)) {
                        final String absoluteOperandName = record.getBasepath(fieldName) + operandName;

                        if (!fieldsToMask.containsKey(operandName)) {
                            final String operandValue = new String(record.getFieldValue(absoluteOperandName));

                            operandsNotToBeMasked.put(absoluteOperandName, new OriginalMaskedValuePair(operandValue, operandValue));
                        }
                    } else {
                        for (String path : record.generatePaths(fieldName)) {
                            final String absoluteOperandName = record.getBasepath(path) + operandName;

                            if (!fieldsToMask.containsKey(operandName)) {
                                final String operandValue = new String(record.getFieldValue(absoluteOperandName));

                                operandsNotToBeMasked.put(absoluteOperandName, new OriginalMaskedValuePair(operandValue, operandValue));
                            }
                        }
                    }
                }
            }
        }

        return operandsNotToBeMasked;
    }

    protected List<String> expandToBeMasked(Iterable<String> fields, MultipathRecord record) {
        List<String> effectivePathsToBeMasked = new ArrayList<>();

        for (String fieldIdentifier : fields) {
            if (record.isSingleElement(fieldIdentifier)) {
                effectivePathsToBeMasked.add(fieldIdentifier);
            } else {
                for (String path : record.generatePaths(fieldIdentifier)) {
                    effectivePathsToBeMasked.add(path);
                }
            }
        }

        return effectivePathsToBeMasked;
    }

    protected Map<String, Tuple<DataMaskingTarget, String>> expandToBeMasked(Map<String, DataMaskingTarget> fields, MultipathRecord record) {
        Map<String, Tuple<DataMaskingTarget, String>> effectivePathsToBeMasked = new HashMap<>();

        for (Map.Entry<String, DataMaskingTarget> field : fields.entrySet()) {
            final String fieldIdentifier = field.getKey();
            final DataMaskingTarget target = field.getValue();

            if (record.isSingleElement(fieldIdentifier)) {
                effectivePathsToBeMasked.put(fieldIdentifier, new Tuple<>(target, fieldIdentifier));
            } else {
                for (String path : record.generatePaths(fieldIdentifier)) {
                    effectivePathsToBeMasked.put(path, new Tuple<>(target, fieldIdentifier));
                }
            }
        }

        return effectivePathsToBeMasked;
    }

    protected boolean existOperandsWithNonFixedPath(Map<String, FieldRelationship> relationships, MultipathRecord record) {
        if (relationships == null || relationships.isEmpty()) {
            return false;
        }

        for (FieldRelationship relationship : relationships.values()) {
            for (RelationshipOperand operand : relationship.getOperands()) {
                if (!record.isSingleElement(operand.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void maskElement(MultipathRecord record, String fieldIdentifier, MaskingProvider provider, String targetPath, FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> processedFields) {
        if (record.isPrimitiveType(fieldIdentifier)) {
            logger.debug("Field reference {} is primitive", fieldIdentifier);

            byte[] fieldValue = record.getFieldValue(fieldIdentifier);
            if (fieldValue != null) {
                String originalValue = new String(fieldValue);

                String maskedValue = maskValue(
                        provider,
                        originalValue,
                        fieldIdentifier,
                        fixRelativePaths(fieldIdentifier, fieldRelationship, record), processedFields);

                record.setFieldValue(targetPath, (maskedValue != null) ? maskedValue.getBytes() : null);

                processedFields.put(fieldIdentifier, new OriginalMaskedValuePair(originalValue, maskedValue));
            } else {
                processedFields.put(fieldIdentifier, new OriginalMaskedValuePair(null, null));
            }
        } else {
            logger.debug("Not primitive, processing if supported by MaskingProvider");
            if (provider.supportsObject()) {
                logger.debug("Complex structure processing is supported by {}", provider.getClass().getCanonicalName());
                record.setFieldValue(
                        fieldIdentifier,
                        provider.mask(
                                record.getFieldObject(fieldIdentifier),
                                fieldIdentifier
                        )
                );
            } else {
                logger.warn("Complex structure processing not supported by {}", provider.getClass().getCanonicalName());
                throw new UnsupportedOperationException("Complex structure processing not supported by " + provider.getClass().getCanonicalName());
            }
            processedFields.put(fieldIdentifier, new OriginalMaskedValuePair(null, null)); // TODO: CHECK THAT IT IS CONSISTENT
        }
    }

    private FieldRelationship fixRelativePaths(String fieldIdentifier, FieldRelationship fieldRelationship, MultipathRecord record) {
        if (null == fieldRelationship) return null;

        return new FieldRelationship(
                fieldRelationship.getValueClass(),
                fieldRelationship.getRelationshipType(),
                fieldIdentifier,
                fixOperandPaths(record.getBasepath(fieldIdentifier), fieldRelationship.getOperands(), record)
        );
    }


    private RelationshipOperand[] fixOperandPaths(String basePath, RelationshipOperand[] operands, MultipathRecord record) {
        RelationshipOperand[] operandsWithFixedPath = new RelationshipOperand[operands.length];

        for (int i = 0; i < operands.length; ++i) {
            operandsWithFixedPath[i] = fixOperandPath(basePath, operands[i], record);
        }

        return operandsWithFixedPath;
    }

    private RelationshipOperand fixOperandPath(String basePath, RelationshipOperand operand, MultipathRecord record) {
        final String operandName = operand.getName();

        if (record.isAbsolute(operandName)) return operand;

        return new RelationshipOperand(
                basePath + operandName,
                operand.getType()
        );
    }

    @Override
    public IdentificationReport identifyTypesStream(InputStream input,
                                                    DataTypeFormat inputFormatType,
                                                    DatasetOptions datasetOptions,
                                                    Collection<Identifier> identifiers,
                                                    int firstN) throws IOException {
        Map<String, Map<String, Counter>> allTypes = new HashMap<>();
        Map<String, Map<String, Counter>> fieldNameTypes = new HashMap<>();

        long recordCount = 0;
        for (final Record recordForCast : extractRecords(input, datasetOptions, firstN)) {
            final MultipathRecord record = (MultipathRecord) recordForCast;

            for (final String generalizedPath : record.getFieldReferencesWithGeneralization()) {
                if (!fieldNameTypes.containsKey(generalizedPath)) {
                    fieldNameTypes.put(generalizedPath, checkFieldName(generalizedPath, identifiers));
                }

                final Map<String, Counter> pathTypes = allTypes.computeIfAbsent(generalizedPath, ignored -> new HashMap<>());

                final Iterable<String> actualPaths = record.generatePaths(generalizedPath);

                for (final String actualPath : actualPaths) {
                    byte[] bValue = record.getFieldValue(actualPath);

                    if (null == bValue) {
                        updateCounter(pathTypes, ProviderType.EMPTY.getName());
                        continue;
                    }

                    final String value = new String(bValue);

                    if (value.isEmpty()) {
                        updateCounter(pathTypes, ProviderType.EMPTY.getName());
                        continue;
                    }

                    int found = 0;

                    for (final Identifier identifier : identifiers) {
                        if (identifier.isOfThisType(value)) {
                            updateCounter(pathTypes, identifier.getType().getName());
                            found += 1;
                        }
                    }

                    if (found == 0) {
                        updateCounter(pathTypes, ProviderType.UNKNOWN.getName());
                    }
                }
            }

            recordCount += 1;
        }

        return assembleReport(
                allTypes,
                fieldNameTypes,
                recordCount);
    }

    private Map<String, Counter> checkFieldName(String generalizedPath, Collection<Identifier> identifiers) {
        Map<String, Counter> types = new HashMap<>();
        final String fieldName = extractFieldName(generalizedPath);
        for (Identifier identifier : identifiers) {
            if (identifier.isAppropriateName(
                    fieldName
            )) {
                updateCounter(types, identifier.getType().getName());
            }
        }

        return types;
    }

    @Override
    protected String extractFieldName(String fieldName) {
        return super.extractFieldName(fieldName);
    }

    @Override
    public Record maskRecord(Record record, MaskingProviderFactory maskingProvidersFactory,
                             Set<String> alreadyMaskedFields, DataMaskingOptions dataMaskingOptions) {
        Map<String, FieldRelationship> relationships = dataMaskingOptions.getPredefinedRelationships();

        final MultipathRecord multipathRecord = (MultipathRecord) record;

        if (existOperandsWithNonFixedPath(relationships, multipathRecord))
            throw new UnsupportedOperationException("Only fixed path operands are supported");


        Map<String, Tuple<DataMaskingTarget, String>> fieldsToMask = expandToBeMasked(dataMaskingOptions.getToBeMasked(), multipathRecord);
        Queue<String> fieldsToProcess = new LinkedList<>(fieldsToMask.keySet());

        Map<String, OriginalMaskedValuePair> processedFieldValues = extractOperandsNotToBeMasked(multipathRecord, relationships, fieldsToMask);

        while (!fieldsToProcess.isEmpty()) {
            String fieldIdentifier = fieldsToProcess.poll();

            FieldRelationship fieldRelationship = findRelationship(relationships, fieldIdentifier, multipathRecord);

            if (needsOtherFieldsToBeProcessed(multipathRecord.getBasepath(fieldIdentifier), fieldRelationship, processedFieldValues, multipathRecord)) {
                fieldsToProcess.add(fieldIdentifier);
                continue;
            }

            Tuple<DataMaskingTarget, String> targetAndPath = fieldsToMask.get(fieldIdentifier);
            DataMaskingTarget target = targetAndPath.getFirst();
            ProviderType fieldType = target.getProviderType();

            final MaskingProvider provider = maskingProvidersFactory.get(targetAndPath.getSecond(), fieldType);

            String targetPath = target.getTargetPath();

            if (!multipathRecord.isSingleElement(targetPath)) targetPath = fieldIdentifier;

            maskElement(multipathRecord, fieldIdentifier, provider, targetPath, fieldRelationship, processedFieldValues);
        }

        return record;
    }

    private FieldRelationship findRelationship(Map<String, FieldRelationship> relationships,
                                               String fieldIdentifier, MultipathRecord record) {

        if (relationships == null || relationships.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, FieldRelationship> relationship : relationships.entrySet()) {
            if (record.isMatching(relationship.getKey(), fieldIdentifier)) {
                return relationship.getValue();
            }
        }
        return null;
    }

    private boolean needsOtherFieldsToBeProcessed(String basePath, FieldRelationship fieldRelationship,
                                                  Map<String, OriginalMaskedValuePair> processedFieldValues, MultipathRecord record) {
        if (null == fieldRelationship) return false;

        for (final RelationshipOperand operand : fieldRelationship.getOperands()) {
            String operandName = operand.getName();

            if (!record.isAbsolute(operandName)) {
                operandName = basePath + operandName;
            }

            if (!processedFieldValues.containsKey(operandName)) return true;
        }

        return false;
    }

    @Override
    protected void performOutputAction(Record record, OutputStream output, Iterable<String> fieldsToSuppress) throws IOException {

        List<String> effectiveFieldToSuppress = expandToBeMasked(fieldsToSuppress, (MultipathRecord) record);

        for (int f = effectiveFieldToSuppress.size() - 1; f >= 0; f--) {
            record.suppressField(effectiveFieldToSuppress.get(f));
        }

        output.write(record.toString().getBytes());
        output.write(System.lineSeparator().getBytes());
    }

}
