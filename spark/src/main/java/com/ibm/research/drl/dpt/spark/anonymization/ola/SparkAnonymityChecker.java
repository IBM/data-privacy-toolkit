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
package com.ibm.research.drl.dpt.spark.anonymization.ola;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.ContentRequirements;
import com.ibm.research.drl.dpt.anonymization.DatasetGeneralizer;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymityMetric;
import com.ibm.research.drl.dpt.anonymization.ola.AnonymityChecker;
import com.ibm.research.drl.dpt.anonymization.ola.LatticeNode;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import com.ibm.research.drl.dpt.spark.utils.SparkEncoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SparkAnonymityChecker implements AnonymityChecker, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SparkAnonymityChecker.class);

    private final Dataset<String> input;
    private final List<ColumnInformation> columnInformationList;
    private final List<Integer> quasiColumns;
    private final List<Integer> sensitiveColumns;
    private final Long inputSize;
    private final char delimiter;
    private final char quoteChar;
    private final List<PrivacyConstraint> privacyConstraints;
    private final List<PrivacyMetric> privacyMetrics;
    private final int privacyConstraintsContentRequirements;

    private final static CsvMapper csvMapper = new CsvMapper();

    public static Tuple2<String, List<PrivacyMetric>> createMapToPair(String s, char delimiter, char quoteChar,
                                                                      List<ColumnInformation> columnInformationList,
                                                                      List<PrivacyMetric> privacyMetrics,
                                                                      List<Integer> quasiColumns,
                                                                      List<Integer> sensitiveColumns,
                                                                      int[] levels,
                                                                      int privacyConstraintsContentRequirements) throws IOException {

        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(delimiter).withQuoteChar(quoteChar);
        JsonNode csvRecord = csvMapper.reader().with(schema).readTree(s);

        List<String> row = DatasetGeneralizer.generalizeRow(csvRecord, columnInformationList, levels);

        StringBuilder stringBuffer = new StringBuilder();

        for (Integer quasiColumn : quasiColumns) {
            String value = row.get(quasiColumn);
            stringBuffer.append(value);
            stringBuffer.append(":");
        }

        String key = stringBuffer.toString();

        List<String> sensitiveValues;

        if (privacyConstraintsContentRequirements == ContentRequirements.NONE) {
            sensitiveValues = Collections.emptyList();
        } else {
            sensitiveValues = new ArrayList<>();
            for (Integer sensitiveColumn : sensitiveColumns) {
                sensitiveValues.add(row.get(sensitiveColumn));
            }
        }

        List<PrivacyMetric> metrics = new ArrayList<>();
        for (PrivacyMetric metric : privacyMetrics) {
            metrics.add(metric.getInstance(sensitiveValues));
        }

        return new Tuple2<>(key, metrics);
    }

    public static boolean checkConstraints(List<PrivacyMetric> privacyMetrics,
                                           List<PrivacyConstraint> privacyConstraints) {

        for (int i = 0; i < privacyConstraints.size(); i++) {
            PrivacyConstraint constraint = privacyConstraints.get(i);
            PrivacyMetric metric = privacyMetrics.get(i);

            if (!constraint.check(metric)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public double calculateSuppressionRate(LatticeNode node) {
        final int[] levels = node.getValues();

        logger.info("Checking node: " + node);

        // Map each string to (key, metrics) tuple encoded as a string pair, group by key, then check constraints
        Dataset<Long> counters = input
                .map((MapFunction<String, Tuple2<String, List<PrivacyMetric>>>) s ->
                        createMapToPair(s, delimiter, quoteChar, columnInformationList, privacyMetrics,
                                quasiColumns, sensitiveColumns, levels, privacyConstraintsContentRequirements),
                        SparkEncoders.javaSerOf(Tuple2.class))
                .groupByKey((MapFunction<Tuple2<String, List<PrivacyMetric>>, String>) Tuple2::_1, Encoders.STRING())
                .reduceGroups((ReduceFunction<Tuple2<String, List<PrivacyMetric>>>) (p1, p2) -> {
                    for (int i = 0; i < p1._2().size(); i++) {
                        p1._2().get(i).update(p2._2().get(i));
                    }
                    return p1;
                })
                .map((MapFunction<Tuple2<String, Tuple2<String, List<PrivacyMetric>>>, Long>) kv -> {
                    Tuple2<String, List<PrivacyMetric>> val = kv._2();
                    if (!checkConstraints(val._2(), privacyConstraints)) {
                        return 0L;
                    }
                    for (PrivacyMetric metric : val._2()) {
                        if (metric instanceof KAnonymityMetric) {
                            return ((KAnonymityMetric) metric).getCount();
                        }
                    }
                    return 0L;
                }, Encoders.LONG())
                .filter((org.apache.spark.api.java.function.FilterFunction<Long>) v -> v > 0);

        Long suppressed = 0L;

        long countersCount = counters.count();

        if (countersCount == 1) {
            suppressed = counters.first();
        } else if (countersCount > 0) {
            suppressed = counters.reduce((org.apache.spark.api.java.function.ReduceFunction<Long>) (a, b) -> a + b);
        }

        logger.info("Suppression for node: " + node.toString() + " is " + (100.0 * (double) suppressed / (double) inputSize));
        return 100.0 * (double) suppressed / (double) inputSize;
    }

    public SparkAnonymityChecker(Dataset<String> input, Long inputSize, List<ColumnInformation> columnInformationList,
                                 List<PrivacyConstraint> privacyConstraints, char delimiter, char quoteChar) {
        this.input = input;
        this.privacyConstraints = privacyConstraints;
        this.inputSize = inputSize;
        this.columnInformationList = columnInformationList;
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
        this.quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.SENSITIVE);
        this.privacyConstraintsContentRequirements = AnonymizationUtils.buildPrivacyConstraintContentRequirements(privacyConstraints);

        this.privacyMetrics = new ArrayList<>();
        for (PrivacyConstraint constraint : privacyConstraints) {
            this.privacyMetrics.add(constraint.getMetricInstance());
        }
    }
}
