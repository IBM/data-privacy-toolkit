/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.spark.export;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import scala.collection.JavaConverters;

import java.util.Collections;
import java.util.List;


public class Export {
    private static void doFileExport(Dataset<Row> dataset, String outputFile,
                                     DataTypeFormat exportFormat, List<String> partitions, boolean appendMode) {
        DataFrameWriter<Row> writer = dataset.write().option("charset", "utf-8");

        if (!partitions.isEmpty()) {
            writer.partitionBy(
                    JavaConverters.asScalaIteratorConverter(partitions.iterator()).asScala().toSeq()
            );
        }

        if (appendMode) {
            writer.mode(SaveMode.Append);
        } else {
            writer.mode(SaveMode.Overwrite);
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
