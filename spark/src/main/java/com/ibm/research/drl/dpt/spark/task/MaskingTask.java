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
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.MaskingOptions;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.*;
import java.util.function.Predicate;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.udf;

public class MaskingTask extends SparkTaskToExecute {
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
        final Set<String> alreadyMaskedFields = new HashSet<>();
        final Queue<String> fieldsToMask = new ArrayDeque<>(this.taskOptions.getToBeMasked().keySet());

        final Collection<String> fieldsToSuppress = getFieldsToSuppress(this.taskOptions.getToBeMasked());

        while (! fieldsToMask.isEmpty()) {
            String fieldName = Objects.requireNonNull(fieldsToMask.poll());

            if (requiresOtherField(fieldName)) {
                fieldsToMask.add(fieldName);

                continue;
            }

            dataset = maskField(dataset, fieldName, factory);

            alreadyMaskedFields.add(fieldName);
        }

        return suppressFields(dataset, fieldsToSuppress);
    }

    private MaskingProviderFactory buildMaskingProviderFactory() {
        final ConfigurationManager configurationManager = ConfigurationManager.load(this.taskOptions.getMaskingProvidersConfig());
        return new MaskingProviderFactory(configurationManager, this.taskOptions.getToBeMasked());
    }

    private Dataset<Row> maskField(Dataset<Row> dataset, String fieldName, MaskingProviderFactory factory) {
        final DataMaskingTarget target = this.taskOptions.getToBeMasked().get(fieldName);
        final MaskingProvider provider = factory.get(fieldName, target.getProviderType());

        DataType targetDataType = retrieveDatatype(dataset.schema(), fieldName);
        UDF1<String, String> maskingAndConvert = provider::mask;

        return dataset.withColumn(target.getTargetPath(),
                udf(maskingAndConvert, DataTypes.StringType).apply(
                        col(fieldName).cast(DataTypes.StringType)
                ).cast(targetDataType));
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

    private boolean requiresOtherField(String fieldsToMask) {
        return false;
    }
}
