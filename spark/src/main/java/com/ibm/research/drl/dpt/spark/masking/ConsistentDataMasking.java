/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.masking;

import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.records.CSVRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.processors.records.RecordFactory;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import com.ibm.research.drl.jsonpath.JSONPathException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaUtils;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import scala.Array;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.JavaConverters;

import java.io.IOException;
import java.util.*;

public class ConsistentDataMasking {
    private static final Logger logger = LogManager.getLogger(ConsistentDataMasking.class);

    public static String updateKeyToRecord(String maskedKey, Row record,
                                           DataMaskingOptions dataMaskingOptions,
                                           final String jsonPath,
                                           Map<String, Integer> fieldPaths) throws IOException {

        Record record1 = RecordUtils.createRecord(record, dataMaskingOptions.getInputFormat(), dataMaskingOptions.getDatasetOptions(), null, fieldPaths, null, false);
        record1.setFieldValue(jsonPath, maskedKey.getBytes());

        return record1.toString();
    }

    public static List<String> writeMaskedKeyForSingleConsistency(String key, String keyFieldName,
                                                                  final Map<String, MaskingProvider> maskingProviders, Iterable<Row> records,
                                                                  final DataMaskingOptions maskingOptions,
                                                                  final String jsonPath,
                                                                  Map<String, Integer> fieldPaths) throws IOException, JSONPathException {

        String maskedKey = maskingProviders.get(keyFieldName).mask(key);

        List<String> output = new ArrayList<>();

        for (Row record : records) {
            String updatedRecord = updateKeyToRecord(maskedKey, record, maskingOptions, jsonPath, fieldPaths);
            output.add(updatedRecord);
        }

        return output;

    }
    private static String groupByPathForGlobalConsistency(
            Tuple3<Row, String, Long> s,
            DataMaskingOptions maskingOptions,
            Map<String, Integer> fieldPaths) throws IOException {

        Row record = s._1();
        String path = s._2();

        Record record1 = RecordUtils.createRecord(record, maskingOptions.getInputFormat(), maskingOptions.getDatasetOptions(), null, fieldPaths, null, false);

        return new String(record1.getFieldValue(path));
    }

    private static Iterable<Tuple3<Row, String, Long>> projectionMapForGlobalConsistency(final Tuple2<Row, Long> s, final Set<String> fieldsInNamespace, final DataMaskingOptions maskingOptions) {
        Row line = s._1();
        Long rowIndex = s._2();

        List<Tuple3<Row, String, Long>> projections = new ArrayList<>();

        for (String field : fieldsInNamespace) {
            Tuple3<Row, String, Long> projection = new Tuple3<>(line, field, rowIndex);
            projections.add(projection);
        }

        return projections;
    }


    public static String mergeRecordObjectsByPath(Iterable<Tuple2<String, String>> maskedRecords,
                                                  DataTypeFormat inputFormatType, DatasetOptions datasetOptions, Map<String, Integer> fieldPaths)
            throws IOException {

        Record firstNode = null;

        for (Tuple2<String, String> t : maskedRecords) {
            String r = t._1();
            String path = t._2();

            Record record = RecordFactory.parseString(r, inputFormatType, datasetOptions, fieldPaths, false);

            if (firstNode == null) {
                firstNode = record;
            } else {
                byte[] updatedValue = record.getFieldValue(path);
                firstNode.setFieldValue(path, updatedValue);
            }
        }

        return firstNode.toString();
    }
    
    private static String mergeMaskedPathsRecord(Tuple2<Long, Iterable<Tuple2<String, String>>> s, Set<String> fieldsInNamespace,
                                                 DataMaskingOptions maskingOptions, Map<String, Integer> fieldPaths) throws IOException, JSONPathException {

        Iterable<Tuple2<String, String>> maskedRecords = s._2();
        return mergeRecordObjectsByPath(maskedRecords, maskingOptions.getInputFormat(), maskingOptions.getDatasetOptions(), fieldPaths);
    }
    
    private static String mergeMaskedPaths(Tuple2<Long, Iterable<Tuple2<String, String>>> s,
                                           Set<String> fieldsInNamespace,
                                           DataMaskingOptions maskingOptions, Map<String, Integer> fieldPaths) throws IOException, JSONPathException {

        return mergeMaskedPathsRecord(s, fieldsInNamespace, maskingOptions, fieldPaths);

    }
    
    public static Tuple2<String, Row> createMapPairForSingleConsistency(Row row,
                                                                        final DataMaskingOptions maskingOptions,
                                                                        String fieldName,
                                                                        Map<String, Integer> fieldPaths) throws IOException {
        Record record = RecordUtils.createRecord(row, maskingOptions.getInputFormat(), maskingOptions.getDatasetOptions(), null, fieldPaths, null, false);
        String key = new String(record.getFieldValue(fieldName));
        return new Tuple2<>(key, row);
    }
    
    
    public static Dataset<Row> doGloballyConsistentMasking(Dataset<Row> dataset, Map<String, DataMaskingTarget> identifiedTypes,
                                                               final Map<String, MaskingProvider> maskingProviders, 
                                                               final Set<String> fieldsInNamespace,
                                                               final Map<String, Integer> fieldPaths, final DataMaskingOptions maskingOptions) {
        JavaRDD<String> maskedRdd = dataset.javaRDD().zipWithIndex()
                .flatMap(s -> projectionMapForGlobalConsistency(s, fieldsInNamespace, maskingOptions).iterator())
                .groupBy(s -> groupByPathForGlobalConsistency(s, maskingOptions, fieldPaths))
                .map(s -> {
                    String key = s._1();
                    Iterable<Tuple3<Row, String, Long>> records = s._2();
                    String maskedKey = null;
                    List<Tuple2<Long, Tuple2<String, String>>> projectedRecords = new ArrayList<>();
                    for (Tuple3<Row, String, Long> r : records) {
                        Row record = r._1();
                        String path = r._2();
                        Long rowIndex = r._3();
                        // we mask the key based on the first field of the namespace
                        if (maskedKey == null) {
                            maskedKey = maskingProviders.get(path).mask(key);
                        }
                        String updatedRecord = updateKeyToRecord(maskedKey, record, maskingOptions, path, fieldPaths);
                        Tuple2<String, String> maskedRecord = new Tuple2<>(updatedRecord, path);
                        Tuple2<Long, Tuple2<String, String>> projectedRecord = new Tuple2<>(rowIndex, maskedRecord);
                        projectedRecords.add(projectedRecord);
                    }
                    return projectedRecords;
                })
                .flatMapToPair(List::iterator)
                .groupByKey()
                .map(longIterableTuple2 -> mergeMaskedPaths(longIterableTuple2, fieldsInNamespace, maskingOptions, fieldPaths));

        JavaRDD<Row> rowRdd = maskedRdd.map(row -> ((CSVRecord) CSVRecord.fromString(row, maskingOptions.getDatasetOptions(), fieldPaths, false)))
                .map(csvRecord -> {
                    List<Object> orderedValues = new ArrayList<>();

                    for (StructField field : dataset.schema().fields()) {
                        orderedValues.add(
                              csvRecord.getFieldValue(field.name())
                        );
                    }

                    return JavaConverters.asScalaIteratorConverter(orderedValues.iterator()).asScala().toSeq();
                })
                .map(Row::fromSeq);

        return dataset.sparkSession().createDataFrame(rowRdd, dataset.schema()); // to be fixed!
    } 
    
    public static Dataset<Row> doConsistentMasking(SparkSession sparkSession, Dataset<Row> dataset,
                                                    final Map<String, MaskingProvider> maskingProviders,
                                                    final String fieldName,
                                                    final Map<String, Integer> fieldPaths,
                                                    final DataMaskingOptions maskingOptions) {

        JavaRDD<String> rdd = dataset.javaRDD().mapToPair(s -> createMapPairForSingleConsistency(s, maskingOptions, fieldName, fieldPaths))
                .groupByKey()
                .map((Function<Tuple2<String, Iterable<Row>>, List<String>>) tuple2 -> {
                    String key = tuple2._1();
                    Iterable<Row> records = tuple2._2();
                    return writeMaskedKeyForSingleConsistency(key, fieldName, maskingProviders, records, maskingOptions, fieldName, fieldPaths);
                }).flatMap((FlatMapFunction<List<String>, String>) List::iterator);
        
        return sparkSession.createDataset(rdd.rdd(), Encoders.STRING()).toDF();


    }
}
