package com.ibm.research.drl.dpt.spark.task;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class MaskingTask implements SparkTaskToExecute {
    @Override
    public Dataset<Row> process(String inputReference) {
        return null;
    }
}
