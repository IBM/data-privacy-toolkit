/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "task")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaskingTask.class, name = "Masking"),
})
public abstract class SparkTaskToExecute {
    public final Dataset<Row> process(String inputReference) {

    }
}
