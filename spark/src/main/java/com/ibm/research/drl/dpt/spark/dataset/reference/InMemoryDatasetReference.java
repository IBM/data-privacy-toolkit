/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.dataset.reference;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.GenericDatasetOptions;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryDatasetReference extends DatasetReference {
    private final List<String> columnNames;
    private final List<List<String>> data;
    private Dataset<Row> dataset;

    public InMemoryDatasetReference(List<List<String>> data, List<String> columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    public InMemoryDatasetReference() {
        this(null, null);
    }

    @Override
    public DataTypeFormat getFormat() {
        return DataTypeFormat.CSV;
    }

    @Override
    public DatasetOptions getOptions() {
        return new GenericDatasetOptions();
    }

    @Override
    public Dataset<Row> readDataset(SparkSession sparkSession, String inputReference) {
        if (null == this.dataset) {
            this.dataset = createDataset(sparkSession);
        }

        return dataset;
    }

    private Dataset<Row> createDataset(SparkSession sparkSession) {
        return sparkSession.createDataFrame(
                createData(),
                createSchema()
        );
    }

    private StructType createSchema() {
        if (null == this.columnNames) throw new IllegalArgumentException();

        return new StructType(this.columnNames.stream().map(name -> new StructField(name, DataTypes.StringType, false, Metadata.empty())).toArray(StructField[]::new));
    }

    private List<Row> createData() {
        if (this.data == null) throw new IllegalArgumentException();

        return this.data.stream().map(dataPoint -> RowFactory.create(dataPoint.toArray())).collect(Collectors.toList());
    }

    @Override
    public void writeDataset(SparkSession sparkSession, Dataset<Row> outputDataset) {

    }

    @Override
    public OutputStream asOutputStream() throws IOException {
        return null;
    }
}
