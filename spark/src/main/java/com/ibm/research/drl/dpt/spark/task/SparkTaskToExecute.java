/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
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
//        @JsonSubTypes.Type(value = MaskingTask.class, name = "Masking"),
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

    public abstract Dataset<Row> process(Dataset<Row> inputDataset);

    public Dataset<Row> readInputDataset(String inputReference) {
        return this.inputDatasetReference.readDataset(getSparkSession(), inputReference);
    }

    public void writeProcessedDataset(Dataset<Row> processedDataset, String optionValue) {

    }
}
