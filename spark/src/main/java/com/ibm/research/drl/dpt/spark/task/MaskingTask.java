/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.MaskingOptions;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.udf;

public class MaskingTask extends SparkTaskToExecute {
    private static final Logger logger = LogManager.getLogger(MaskingTask.class);
    private static final String[] PREFIX = {
            "___",
            "###",
            "PREFIX_",
            UUID.randomUUID().toString().substring(0, 5)
    };
    private final MaskingOptions taskOptions;

    @JsonCreator
    public MaskingTask(
            @JsonProperty("task") String task,
            @JsonProperty("inputOptions") DatasetReference inputOptions,
            @JsonProperty("inputOptions") DatasetReference outputOptions,
            @JsonProperty("inputOptions") MaskingOptions taskOptions) {
        super(task, inputOptions, outputOptions);
        this.taskOptions = taskOptions;
    }

    @Override
    public Dataset<Row> process(Dataset<Row> dataset) {
        final MaskingProviderFactory factory = buildMaskingProviderFactory();

        final Queue<String> fieldsToMask = new ArrayDeque<>(this.taskOptions.getToBeMasked().keySet());

        final String prefix = findPrefix(dataset.columns());

        dataset = expandDatasetWithFieldsToPreserve(dataset, prefix);

        final Collection<String> fieldsToSuppress = getFieldsToSuppress(this.taskOptions.getToBeMasked());
        final Set<String> alreadyMaskedFields = new HashSet<>();

        while (! fieldsToMask.isEmpty()) {
            String fieldName = Objects.requireNonNull(fieldsToMask.poll());

            if (requiresOtherField(fieldName, alreadyMaskedFields)) {
                logger.trace("Delaying processing of " + fieldName);

                fieldsToMask.add(fieldName);

                continue;
            }

            dataset = maskField(dataset, fieldName, factory, prefix);

            alreadyMaskedFields.add(fieldName);
        }

        return suppressFields(dataset, fieldsToSuppress);
    }

    private Dataset<Row> expandDatasetWithFieldsToPreserve(Dataset<Row> dataset, String prefix) {
        final Collection<String> requiresOriginal = findFieldsMaskedButAlsoRequiredAsOriginal(dataset.columns(), this.taskOptions.getToBeMasked(), this.taskOptions.getPredefinedRelationships());

        for (String requiredOriginal : requiresOriginal) {
            dataset = dataset.withColumn(prefix + requiredOriginal, dataset.col(requiredOriginal));
        }

        return dataset;
    }

    private Collection<String> findFieldsMaskedButAlsoRequiredAsOriginal(String[] columnNames, Map<String, DataMaskingTarget> columnToBeMasked, Map<String, FieldRelationship> relationships) {
        // TODO: this operation can be further refined if we also consider the relationship type

        final Set<String> operands = relationships.values().stream()
                .map(FieldRelationship::getOperands)
                .flatMap(Arrays::stream)
                .map(RelationshipOperand::getName)
                .collect(Collectors.toSet());

        return Arrays.stream(columnNames).filter(columnToBeMasked::containsKey)
                .filter(operands::contains)
                .collect(Collectors.toList());
    }

    private String findPrefix(String[] columnNames) {
        Set<String> names = new HashSet<>(Arrays.asList(columnNames));

        for (String prefix : MaskingTask.PREFIX) {
            boolean good = true;

            for (String columnName : columnNames) {
                if (columnName.startsWith(prefix) || names.contains(prefix + columnName)) {
                    good = false;
                    break;
                }
            }

            if (good) {
                return prefix;
            }
        }

        throw new RuntimeException("Unable to find a good candidate as prefix");
    }

    private MaskingProviderFactory buildMaskingProviderFactory() {
        final ConfigurationManager configurationManager = ConfigurationManager.load(this.taskOptions.getMaskingProvidersConfig());
        return new MaskingProviderFactory(configurationManager, this.taskOptions.getToBeMasked());
    }

    private Dataset<Row> maskField(Dataset<Row> dataset, String fieldName, MaskingProviderFactory factory, String prefix) {
        final DataMaskingTarget target = this.taskOptions.getToBeMasked().get(fieldName);
        final MaskingProvider provider = factory.get(fieldName, target.getProviderType());
        final DataType targetDataType = retrieveDatatype(dataset.schema(), fieldName);
        final FieldRelationship relationship = taskOptions.getPredefinedRelationships().get(fieldName);

        if (relationship == null) {
            UDF1<String, String> mask = provider::mask;

            return dataset.withColumn(target.getTargetPath(),
                    udf(mask, DataTypes.StringType).apply(
                            dataset.col(fieldName).cast(DataTypes.StringType)
                    ).cast(targetDataType));
        } else {
            switch (relationship.getRelationshipType()) {

                case KEY:
                    String keyFieldName = relationship.getOperands()[0].getName();

                    UDF2<String, String, String> keyedUDF = provider::maskWithKey;

                    return dataset.withColumn(target.getTargetPath(),
                                        udf(keyedUDF, DataTypes.StringType).apply(
                                        dataset.col(fieldName).cast(DataTypes.StringType), dataset.col(prefix + keyFieldName).cast(DataTypes.StringType)
                                    ).cast(targetDataType));
                case DISTANCE:
                    String relativeDistanceFieldName = relationship.getOperands()[0].getName();
                    UDF2<String, String, String> distanceUDF = provider::maskDistance;
                    return dataset.withColumn(target.getTargetPath(),
                            udf(distanceUDF, DataTypes.StringType).apply(
                                dataset.col(fieldName).cast(DataTypes.StringType), dataset.col
                            ).cast(targetDataType);
                case GREP_AND_MASK:
                case SUM:
                case SUM_APPROXIMATE:
                case PRODUCT:
                case EQUALS:
                case GREATER:
                case LESS:
                case LINKED:
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private DataType retrieveDatatype(StructType schema, String fieldName) {
        return schema.fields()[
                schema.fieldIndex(fieldName)
                ].dataType();
    }

    private boolean isSameTargetField(String fieldName, String targetPath) {
        // strong assumption, works with traditional dataframes
        return fieldName.equals(targetPath);
    }

    private Dataset<Row> suppressFields(Dataset<Row> dataset, Collection<String> fieldsToSuppress) {
        return dataset.select(
                findRemainingColumnNames(dataset.columns(), fieldsToSuppress)
        );
    }

    private Column[] findRemainingColumnNames(String[] columns, Collection<String> fieldsToSuppress) {
        return Arrays.stream(columns).filter(
                ((Predicate<String>) fieldsToSuppress::contains).negate()
        ).map(functions::col).toArray(Column[]::new);
    }

    private Collection<String> getFieldsToSuppress(Map<String, DataMaskingTarget> maskingTargets) {
        List<String> fieldsToSuppress = new ArrayList<>();
        for (Map.Entry<String, DataMaskingTarget> toBeMasked : maskingTargets.entrySet()) {
            if (toBeMasked.getValue().getProviderType().equals(ProviderType.SUPPRESS_FIELD)) {
                fieldsToSuppress.add(toBeMasked.getKey());
            }
        }
        return fieldsToSuppress;
    }

    private boolean requiresOtherField(String fieldsToMask, Collection<String> alreadyMaskedFields) {
        FieldRelationship fieldRelationship = this.taskOptions.getPredefinedRelationships().get(fieldsToMask);

        if (fieldRelationship != null) {
            for (RelationshipOperand operand : fieldRelationship.getOperands()) {
                if (!alreadyMaskedFields.contains(operand.getName())) {
                    return true;
                }
            }
        }

        return false;
    }
}
