/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.MaskingOptions;

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
}
