/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class IdentificationTask extends SparkTaskToExecute {
    private static final Logger logger = LogManager.getLogger(IdentificationTask.class);

    public IdentificationTask(String task, DatasetReference inputOptions, DatasetReference outputOptions) {
        super(task, inputOptions, outputOptions);
    }
}
