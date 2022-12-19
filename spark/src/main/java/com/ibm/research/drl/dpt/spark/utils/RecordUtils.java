/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.spark.record.RowRecord;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.util.*;


public class RecordUtils {

    public static List<String> getFieldClasses(StructField[] fields) {
        List<String> fieldTypes = new ArrayList<>();
        for(StructField field: fields) {
            DataType dt = field.dataType();
            String className = dt.getClass().getSimpleName().replace("$", "");
            fieldTypes.add(className);
        }
        
        return fieldTypes;
    }
    
    public static Record createRecord(Row value, DataTypeFormat inputFormat, DatasetOptions datasetOptions,
                                      List<String> fieldNames, Map<String, Integer> fieldMap, List<String> fieldTypes, boolean isHeader) throws IOException {
        switch (inputFormat) {
            case CSV:
            case PARQUET:
                return RowRecord.fromRow(value, fieldNames, fieldMap, fieldTypes);
            default:
//                return RecordFactory.parseString(value.getString(0), inputFormat, datasetOptions, fieldMap, false);
                throw new RuntimeException(String.format("Unsupported format %s", inputFormat));
        }
    }

    public static Map<String,Integer> createFieldMap(List<String> fieldNames) {
        if (fieldNames == null) {
            return null;
        }

        Map<String, Integer> fieldMap = new HashMap<>();

        for(int i = 0; i < fieldNames.size(); i++) {
            fieldMap.put(fieldNames.get(i), i);
        }

        return fieldMap;
    }

    public static Map<String,Integer> createFieldMap(StructType schema) {
        if (schema == null || schema.size() == 0) {
            return Collections.emptyMap();
        }

        Map<String, Integer> fieldMap = new HashMap<>();

        final StructField[] fields = schema.fields();
        for (int i = 0; i < fields.length; ++i) {
            final StructField field = fields[i];
            fieldMap.put(field.name(), i);
        }

        return fieldMap;
    }
}
