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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryDatasetReference extends DatasetReference {
    private final List<String> columnNames;
    private final List<List<String>> data;
    private Dataset<Row> dataset;

    public InMemoryDatasetReference(List<List<String>> data, List<String> columnNames) {
        if (data != null && columnNames != null) {
            if (!data.isEmpty()) {
                for (List<String> row : data) {
                    if (row.size() != columnNames.size())
                        throw new IllegalArgumentException("data rows and columnNames must have same size");
                }
            }

            this.data = data;
            this.columnNames = columnNames;
        } else {
            throw new IllegalArgumentException("Data or column names are undefined");
        }
    }

    public InMemoryDatasetReference() {
        this(Collections.emptyList(), Collections.emptyList());
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
    public void writeDataset(Dataset<Row> outputDataset, String path) {
        this.dataset = outputDataset;
    }

    @Override
    public String toString() {
        if (this.dataset != null) {
            return this.dataset.toString();
        } else {
            if (this.data != null && this.columnNames != null) {
                Stream<String> headers = Stream.of(String.join(",", columnNames));
                Stream<String> dataRows = this.data.stream().map(row -> String.join(",", row));

                return Stream.concat(headers, dataRows).collect(Collectors.joining("\n"));
            }
        }
        return "InMemoryDatasetReference[NotInitialized]";
    }
}
