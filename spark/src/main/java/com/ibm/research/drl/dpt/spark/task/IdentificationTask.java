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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class IdentificationTask extends SparkTaskToExecute {
    private static final Logger logger = LogManager.getLogger(IdentificationTask.class);
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
            List<IdentifiedType> detectedTypes = dataset.toJavaRDD()
                    .mapToPair(row -> new Tuple2<>(row.get(row.fieldIndex(fieldName)), 1L))
                    .reduceByKey(Long::sum)
                    .flatMapToPair(valueCount -> {
                        final List<Tuple2<ProviderType, Long>> matches = new ArrayList<>();

                        final String value = valueCount._1.toString();

                        if (value.isEmpty() || value.isBlank()) {
                            return List.of(new Tuple2<>(ProviderType.EMPTY, valueCount._2)).iterator();
                        }

                        for (Identifier identifier : this.identifiers.availableIdentifiers()) {
                            if (identifier.isOfThisType(value)) {
                                matches.add(new Tuple2<>(identifier.getType(), valueCount._2));
                            }
                        }

                        if (matches.isEmpty()) {
                            matches.add(new Tuple2<>(ProviderType.UNKNOWN, valueCount._2));
                        }

                        return matches.iterator();
                    })
                    .reduceByKey(Long::sum)
                    .map( valueCount -> new IdentifiedType(valueCount._1.name(), valueCount._2))
                    .collect();

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
            ).collect(Collectors.toList()),
            new StructType(new StructField[]{
                    new StructField("Field Name", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("Best Type", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("Proof", DataTypes.createMapType(DataTypes.StringType, DataTypes.LongType), false, Metadata.empty())
            })
        );
    }
}
