/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.dataset.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.spark.export.Export;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileDatasetReference extends DatasetReference {
    private final String datasourceURL;
    private final AuthenticationCredential credentials;
    private final String basePath;
    private final DataTypeFormat format;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "format"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV")
    })
    private final DatasetOptions options;
    private final String partitionBy;
    private final boolean append;

    @JsonCreator
    public FileDatasetReference(
            @JsonProperty("datasourceURL") String datasourceURL,
            @JsonProperty("credentials") AuthenticationCredential credentials,
            @JsonProperty("basePath") String basePath,
            @JsonProperty("format") DataTypeFormat format,
            @JsonProperty("options") DatasetOptions options,
            @JsonProperty("partitionBy") String partitionBy,
            @JsonProperty("append") boolean append
    ) {
        this.datasourceURL = datasourceURL;
        this.credentials = credentials;
        this.basePath = basePath;
        this.format = format;
        this.options = options;
        this.partitionBy = partitionBy;
        this.append = append;
    }

    public FileDatasetReference(
            String datasourceURL,
            String basePath,
            DataTypeFormat format,
            DatasetOptions options,
            String partitionBy,
            boolean append
    ) {
        this(
                datasourceURL,
                null,
                basePath,
                format,
                options,
                partitionBy,
                append
        );
    }

    public FileDatasetReference(
            String datasourceURL,
            DataTypeFormat format,
            DatasetOptions options,
            String partitionBy,
            boolean append
    ) {
        this(
                datasourceURL,
                null,
                null,
                format,
                options,
                partitionBy,
                append
        );
    }

    public String getDatasourceURL() {
        return datasourceURL;
    }

    public boolean isDatasourceURLRemote() { return datasourceURL != null && datasourceURL.toLowerCase().startsWith("hdfs://"); }

    public AuthenticationCredential getCredentials() {
        return credentials;
    }

    public String getBasePath() {
        return basePath;
    }

    public DataTypeFormat getFormat() {
        return format;
    }

    public DatasetOptions getOptions() {
        return options;
    }

    @Override
    public Dataset<Row> readDataset(SparkSession sparkSession, String inputReference) {
        throw new NotImplementedException();
    }

    public String getPartitionBy() {
        return partitionBy;
    }

    public boolean isAppend() {
        return append;
    }

    public Dataset<Row> readDataset(SparkSession sparkSession) {
        return SparkUtils.createDataset(sparkSession, datasourceURL, format, options, basePath);
    }

    public void writeDataset(SparkSession sparkSession, Dataset<Row> outputDataset) {
        // Prepare partitions
        List<String> partitions;
        if (this.partitionBy != null && !this.partitionBy.isEmpty()) {
            partitions = Arrays.asList(this.partitionBy.split(","));
        } else {
            partitions = Collections.emptyList();
        }

        Export.doExport(outputDataset, format, datasourceURL, partitions, this.append);
    }

    @Override
    public OutputStream asOutputStream() throws IOException {
        return isDatasourceURLRemote() ? SparkUtils.createHDFSOutputStream(datasourceURL) : new FileOutputStream(datasourceURL);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("datasourceURL", datasourceURL)
                .append("credentials", credentials)
                .append("basePath", basePath)
                .append("format", format)
                .append("options", options)
                .append("partitionBy", partitionBy)
                .append("append", append)
                .toString();
    }
}
