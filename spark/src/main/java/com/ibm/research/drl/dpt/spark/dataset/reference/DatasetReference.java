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
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "referenceType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileDatasetReference.class, name = "File"),
        @JsonSubTypes.Type(value = DatabaseDatasetReference.class, name = "DB")
})
public abstract class DatasetReference {
    public abstract DataTypeFormat getFormat();

    public abstract DatasetOptions getOptions();

    public abstract Dataset<Row> readDataset(SparkSession sparkSession);

    public abstract void writeDataset(SparkSession sparkSession, Dataset<Row> outputDataset);

    public abstract OutputStream asOutputStream() throws IOException;

    public static DatasetReference fromFile(String path) throws IOException {
        try (InputStream inputStream = SparkUtils.readFile(path)) {
            String lowerCasePath = path.toLowerCase();
            final ObjectMapper mapper;

            if (lowerCasePath.endsWith(".json")) {
                return fromInputStream(inputStream, "json");
            } else if (lowerCasePath.endsWith(".yaml") || lowerCasePath.endsWith(".yml")) {
                return fromInputStream(inputStream, "yaml");
            } else {
                throw new IllegalArgumentException("Unknown extension " + path);
            }
        }
    }

    public static DatasetReference fromInputStream(InputStream inputStream, String format) throws IOException {
        final ObjectMapper mapper;

        if (format.equals("json")) {
            mapper = new ObjectMapper();
        } else if (format.equals("yaml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            throw new IllegalArgumentException("Unknown format " + format);
        }

        // We need this for configurations as for example Parquet, where Format=PARQUET but there is no need for the linked property Options
        // i.e. it can be omitted from the json
        mapper.configure(
                DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY,
                false);

        // Wee need this for now - something changed with the latest 2.11.3 and it now fails by default with unknown properties
        // i.e. for now we need mixed-json that can be deserialized in two different classes with different props
        mapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

        return mapper.readValue(inputStream, DatasetReference.class);
    }
}
