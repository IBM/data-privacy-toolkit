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
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.IdentificationOptions;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import com.ibm.research.drl.dpt.util.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IdentificationTask extends SparkTaskToExecute {
    private static final Logger logger = LoggerFactory.getLogger(IdentificationTask.class);
    private final IdentificationOptions taskOptions;
    private final IdentifierFactory identifiers;

    @JsonCreator
    public IdentificationTask(
            @JsonProperty("task") String task,
            @JsonProperty("inputOptions") DatasetReference inputOptions,
            @JsonProperty("outputOptions") DatasetReference outputOptions,
            @JsonProperty("taskOptions") IdentificationOptions taskOptions
    ) {
        super(task, inputOptions, outputOptions);
        this.taskOptions = taskOptions;
        this.identifiers = IdentifierFactory.initializeIdentifiers(taskOptions.getIdentifiers());
    }

    @Override
    public Dataset<Row> process(Dataset<Row> dataset) {
        final String[] fieldNames = dataset.columns();
        final Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());

        int n = taskOptions.getFirstN();
        if (n > 0) {
            logger.info("Using only first " + n + " records");
            dataset = dataset.limit(n);
        } else {
            double sample = taskOptions.getSampleSize();

            if (sample > 0) {
                logger.info("Using only a sample of size " + sample + "");
                dataset = dataset.sample(false, sample);
            }
        }

        Map<String, List<IdentifiedType>> fieldsProcessed = new HashMap<>(fieldNames.length);

        for (String fieldName : fieldNames) {
            List<IdentifiedType> detectedTypes = dataset
                    .groupBy(org.apache.spark.sql.functions.col(fieldName).cast(org.apache.spark.sql.types.DataTypes.StringType))
                    .count()
                    .flatMap((org.apache.spark.api.java.function.FlatMapFunction<Row, Tuple2<ProviderType, Long>>) valueCount -> {
                        final List<Tuple2<ProviderType, Long>> matches = new ArrayList<>();
                        final String value = valueCount.isNullAt(0) ? "" : valueCount.getString(0);
                        final long count = valueCount.getLong(1);

                        if (value.isEmpty() || value.isBlank()) {
                            return List.of(new Tuple2<>(ProviderType.EMPTY, count)).iterator();
                        }

                        for (Identifier identifier : this.identifiers.availableIdentifiers()) {
                            if (identifier.isOfThisType(value)) {
                                matches.add(new Tuple2<>(identifier.getType(), count));
                            }
                        }

                        if (matches.isEmpty()) {
                            matches.add(new Tuple2<>(ProviderType.UNKNOWN, count));
                        }

                        return matches.iterator();
                    }, com.ibm.research.drl.dpt.spark.utils.SparkEncoders.javaSerOf(Tuple2.class))
                    .groupByKey((org.apache.spark.api.java.function.MapFunction<Tuple2<ProviderType, Long>, String>) t -> t._1().name(), org.apache.spark.sql.Encoders.STRING())
                    .reduceGroups((org.apache.spark.api.java.function.ReduceFunction<Tuple2<ProviderType, Long>>) (a, b) -> new Tuple2<>(a._1(), a._2() + b._2()))
                    .map((org.apache.spark.api.java.function.MapFunction<Tuple2<String, Tuple2<ProviderType, Long>>, IdentifiedType>) kv ->
                            new IdentifiedType(kv._2()._1().name(), kv._2()._2()),
                            org.apache.spark.sql.Encoders.javaSerialization(IdentifiedType.class))
                    .collectAsList();

            fieldsProcessed.put(fieldName, detectedTypes);
        }

        Map<String, IdentifiedType> identifiedTypes = IdentifierUtils.getIdentifiedType(fieldsProcessed, dataset.count(), this.taskOptions.getConfiguration());

        return dataset.sparkSession().createDataFrame(
            identifiedTypes.entrySet().stream().map(entry -> RowFactory.create(
                    entry.getKey(),
                    entry.getValue().getTypeName(),
                    fieldsProcessed.get(entry.getKey()).stream().collect(Collectors.toMap(
                            IdentifiedType::getTypeName,
                            IdentifiedType::getCount
                    )))
            ).toList(),
            new StructType(new StructField[]{
                    new StructField("Field Name", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("Best Type", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("Proof", DataTypes.createMapType(DataTypes.StringType, DataTypes.LongType), false, Metadata.empty())
            })
        );
    }
}
