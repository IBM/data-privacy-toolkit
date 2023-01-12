/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "task")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaskingTask.class, name = "Masking"),
        @JsonSubTypes.Type(value = IdentificationTask.class, name = "Identification"),
})
public abstract class SparkTaskToExecute {
    private static final Logger logger = LogManager.getLogger(SparkTaskToExecute.class);

    private final String task;
    private final DatasetReference inputOptions;
    private final DatasetReference outputOptions;

    public SparkTaskToExecute(
            String task,
            DatasetReference inputOptions,
            DatasetReference outputOptions
    ) {
        this.task = task;
        this.inputOptions = inputOptions;
        this.outputOptions = outputOptions;
    }
}
