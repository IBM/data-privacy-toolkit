/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.export;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.Collections;
import java.util.List;


public class Export {
    private static <T> Seq<T> toSeq(List<T> values) {
       return JavaConverters.asScalaIteratorConverter(values.iterator()).asScala().toSeq();
    }

    private static void doFileExport(Dataset<Row> dataset, String outputFile,
                                     DataTypeFormat exportFormat, List<String> partitions, boolean appendMode) {

        DataFrameWriter<Row> writer = dataset.write().option("charset", "utf-8");

        if (!partitions.isEmpty()) {
            writer.partitionBy(toSeq(partitions));
        }

        if (appendMode) {
            writer.mode(SaveMode.Append);
        }

        switch (exportFormat) {
            case CSV:
                writer.csv(outputFile);
                break;
            case PARQUET:
                writer.parquet(outputFile);
                break;
            default:
                writer.text(outputFile);
                break;
        }
    }

    public static void doExport(JavaRDD<String> rdd, String outputFile) {
        rdd.saveAsTextFile(outputFile);

    }

    public static void doExport(Dataset<Row> dataset, DataTypeFormat exportFormat, String outputFile) {
         doFileExport(dataset, outputFile, exportFormat, Collections.emptyList(), false);
    }

    public static void doExport(Dataset<Row> dataset, DataTypeFormat exportFormat, String outputFile, List<String> partitions, boolean appendMode) {
        doFileExport(dataset, outputFile, exportFormat, partitions, appendMode);
    }



}
