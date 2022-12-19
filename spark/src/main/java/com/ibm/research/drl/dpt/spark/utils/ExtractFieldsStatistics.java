/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.functions.col;


public class ExtractFieldsStatistics {
    public static Map<String, Long> extractCardinalities(Dataset<Row> dataset) {
        final String[] columnNames = dataset.columns();
        final Map<String, Long> cardinalities = new HashMap<>(columnNames.length);

        for (final String columnName : columnNames) {
            cardinalities.put(columnName, dataset.select(col(columnName)).distinct().count());
        }

        return cardinalities;
    }
}
