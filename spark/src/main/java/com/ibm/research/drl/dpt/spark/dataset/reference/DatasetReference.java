/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.dataset.reference;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "referenceType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileDatasetReference.class, name = "File"),
        @JsonSubTypes.Type(value = DatabaseDatasetReference.class, name = "DB")
})
public abstract class DatasetReference implements Serializable {
    public abstract Dataset<Row> readDataset(SparkSession sparkSession, String inputReference);

    public abstract void writeDataset(Dataset<Row> outputDataset, String path);
}
