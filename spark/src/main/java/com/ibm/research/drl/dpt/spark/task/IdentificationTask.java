/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.task.option.IdentificationOptions;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.*;

import static org.apache.spark.sql.functions.col;

public class IdentificationTask extends SparkTaskToExecute {
    private static final Logger logger = LogManager.getLogger(IdentificationTask.class);
    private final IdentificationOptions taskOptions;

    @JsonCreator
    public IdentificationTask(
            @JsonProperty("task") String task,
            @JsonProperty("inputOptions") DatasetReference inputOptions,
            @JsonProperty("outputOptions") DatasetReference outputOptions,
            @JsonProperty("taskOptions") IdentificationOptions taskOptions
    ) {
        super(task, inputOptions, outputOptions);
        this.taskOptions = taskOptions;
    }

    @Override
    public Dataset<Row> process(Dataset<Row> dataset) {
        final List<String> fieldNames = Arrays.asList(dataset.columns());
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

        for (String fieldName : fieldNames) {
            Dataset<Row> aggregateDistinct = dataset.select(col(fieldName)).groupBy().count();
//            aggregateDistinct.flatMap(
//                f -> true
//            );
        }

        return null;
    }
}
