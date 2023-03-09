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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "task")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaskingTask.class, name = "Masking"),
        @JsonSubTypes.Type(value = IdentificationTask.class, name = "Identification"),
})
public abstract class SparkTaskToExecute implements Serializable {
    private static final Logger logger = LogManager.getLogger(SparkTaskToExecute.class);

    private final String task;
    private final DatasetReference inputDatasetReference;
    private final DatasetReference outputDatasetReference;
    private final SparkSession sparkSession;

    public SparkTaskToExecute(
            String task,
            DatasetReference inputDatasetReference,
            DatasetReference outputDatasetReference
    ) {
        this.task = task;
        this.inputDatasetReference = inputDatasetReference;
        this.outputDatasetReference = outputDatasetReference;

        this.sparkSession = SparkUtils.createSparkSession(this.task);
    }

    protected SparkSession getSparkSession() {
        return this.sparkSession;
    }

    public abstract Dataset<Row> process(Dataset<Row> dataset);

    public Dataset<Row> readInputDataset(String input) {
        return this.inputDatasetReference.readDataset(getSparkSession(), input);
    }

    public void writeProcessedDataset(Dataset<Row> dataset, String output) {
        this.outputDatasetReference.writeDataset(dataset, output);
    }
}
