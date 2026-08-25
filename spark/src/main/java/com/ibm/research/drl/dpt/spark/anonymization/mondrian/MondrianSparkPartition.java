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
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.mondrian.CategoricalSplitStrategy;
import com.ibm.research.drl.dpt.anonymization.mondrian.Interval;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import com.ibm.research.drl.dpt.spark.utils.SparkEncoders;
import scala.Tuple2;
import scala.Tuple4;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MondrianSparkPartition implements Serializable {

    private final Dataset<String[]> member;
    private final List<String> middle;
    private final List<Interval> width;
    private final int[] allow;
    private final int qiLen;
    private final List<ColumnInformation> columnInformationList;
    private final List<Integer> quasiColumns;
    private final List<Integer> nonQuasiColumns;
    private final List<Integer> sensitiveColumns;
    private final List<PrivacyConstraint> privacyConstraints;
    private boolean isAnon;
    private final CategoricalSplitStrategy categoricalSplitStrategy;
    private final List<PrivacyMetric> privacyMetrics;
    private final int privacyConstraintsContentRequirements;

    public boolean isSplittable() {
        int sum = 0;
        for (int anAllow : allow) {
            sum += anAllow;
        }
        return sum != 0;
    }

    public Dataset<String[]> getMember() {
        return this.member;
    }

    public boolean isAnonymous() {
        return isAnon;
    }

    public void setAnonymous(boolean value) {
        this.isAnon = value;
    }

    public List<String> getMiddle() {
        return middle;
    }

    public long size() {
        return member.count();
    }

    public void disallow(int quasiIndex) {
        allow[quasiIndex] = 0;
    }

    public long length() {
        return this.member.count();
    }

    private boolean checkConstraints(PrivacyMetric metric) {
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (!privacyConstraint.check(metric)) {
                return false;
            }
        }
        return true;
    }

    public static String generateMiddleKey(double low, double high) {
        if (low == high) {
            return Double.toString(low);
        }
        return low + "-" + high;
    }

    private List<PrivacyMetric> createMapToPair(String[] s, List<ColumnInformation> columnInformationList,
                                                List<PrivacyMetric> privacyMetrics, List<Integer> quasiColumns,
                                                List<Integer> sensitiveColumns, int privacyConstraintsContentRequirements) throws IOException {
        List<String> sensitiveValues;

        if (privacyConstraintsContentRequirements == ContentRequirements.NONE) {
            sensitiveValues = Collections.emptyList();
        } else {
            sensitiveValues = new ArrayList<>();
            for (Integer sensitiveColumn : sensitiveColumns) {
                sensitiveValues.add(s[sensitiveColumn]);
            }
        }

        List<PrivacyMetric> metrics = new ArrayList<>();
        for (PrivacyMetric metric : privacyMetrics) {
            metrics.add(metric.getInstance(sensitiveValues));
        }

        return metrics;
    }

    private boolean checkPartitionForConstraints(Dataset<String[]> input) {
        List<PrivacyMetric> metrics = input
                .map((MapFunction<String[], List<PrivacyMetric>>) s ->
                        createMapToPair(s, columnInformationList, privacyMetrics, quasiColumns, sensitiveColumns, privacyConstraintsContentRequirements),
                        SparkEncoders.javaSerOf(List.class))
                .reduce((org.apache.spark.api.java.function.ReduceFunction<List<PrivacyMetric>>) (p1, p2) -> {
                    for (int i = 0; i < p1.size(); i++) {
                        PrivacyMetric m1 = p1.get(i);
                        PrivacyMetric m2 = p2.get(i);
                        m1.update(m2);
                    }
                    return p1;
                });

        for (int i = 0; i < privacyConstraints.size(); i++) {
            PrivacyMetric metric = metrics.get(i);
            if (!privacyConstraints.get(i).check(metric)) {
                return false;
            }
        }

        return true;
    }

    public List<MondrianSparkPartition> splitNumericalMondrianLike(int quasiIndex, int level) {
        int columnIndex = this.quasiColumns.get(quasiIndex);

        List<PrivacyMetric> privacyMetrics = new ArrayList<>();
        for (PrivacyConstraint constraint : privacyConstraints) {
            privacyMetrics.add(constraint.getMetricInstance());
        }

        List<Tuple2<String, List<PrivacyMetric>>> valueMetricPairs = this.member
                .map((MapFunction<String[], Tuple2<String, List<PrivacyMetric>>>) record -> {
                    String value = record[columnIndex];
                    List<PrivacyMetric> pMetrics = createMapToPair(record, columnInformationList, privacyMetrics, quasiColumns, sensitiveColumns, privacyConstraintsContentRequirements);
                    return new Tuple2<>(value, pMetrics);
                }, SparkEncoders.javaSerOf(Tuple2.class))
                .groupByKey((MapFunction<Tuple2<String, List<PrivacyMetric>>, String>) Tuple2::_1, Encoders.STRING())
                .reduceGroups((org.apache.spark.api.java.function.ReduceFunction<Tuple2<String, List<PrivacyMetric>>>) (p1, p2) -> {
                    for (int i = 0; i < p1._2().size(); i++) {
                        p1._2().get(i).update(p2._2().get(i));
                    }
                    return p1;
                })
                .map((MapFunction<Tuple2<String, Tuple2<String, List<PrivacyMetric>>>, Tuple2<String, List<PrivacyMetric>>>) kv -> kv._2(),
                        SparkEncoders.javaSerOf(Tuple2.class))
                .sort(org.apache.spark.sql.functions.col("value"))
                .collectAsList();

        return null;
    }

    public List<MondrianSparkPartition> splitNumerical(int quasiIndex, int level) {
        int columnIndex = this.quasiColumns.get(quasiIndex);

        Interval partitionWidthInfo = width.get(columnIndex);

        double median = partitionWidthInfo.getMedian();

        middle.set(columnIndex, generateMiddleKey(partitionWidthInfo.getLow(), partitionWidthInfo.getHigh()));
        width.set(columnIndex, new Interval(partitionWidthInfo.getLow(), partitionWidthInfo.getHigh(), median));

        Tuple2<Tuple4<Dataset<String[]>, Double, Double, Double>, Tuple4<Dataset<String[]>, Double, Double, Double>> subPartitions =
                MondrianSparkUtils.splitNumericalByMedian(this.member, columnIndex, median);

        if (subPartitions._1._1().isEmpty() || subPartitions._2._1().isEmpty()) {
            return Collections.emptyList();
        }

        if (!checkPartitionForConstraints(subPartitions._1._1()) || !checkPartitionForConstraints(subPartitions._2._1())) {
            return Collections.emptyList();
        }

        subPartitions._1._1().cache();
        subPartitions._2._1().cache();
        this.member.unpersist();

        double leftMin = subPartitions._1._2();
        double leftMax = subPartitions._1._3();
        double leftMedian = subPartitions._1._4();

        double rightMin = subPartitions._2._2();
        double rightMax = subPartitions._2._3();
        double rightMedian = subPartitions._2._4();

        List<String> leftMiddle = new ArrayList<>(middle);
        leftMiddle.set(columnIndex, generateMiddleKey(leftMin, leftMax));

        List<String> rightMiddle = new ArrayList<>(middle);
        rightMiddle.set(columnIndex, generateMiddleKey(rightMin, rightMax));

        List<Interval> leftWidth = copyList(width);
        leftWidth.set(columnIndex, new Interval(leftMin, leftMax, leftMedian));

        List<Interval> rightWidth = copyList(width);
        rightWidth.set(columnIndex, new Interval(rightMin, rightMax, rightMedian));

        MondrianSparkPartition lhs = new MondrianSparkPartition(subPartitions._1._1(), leftMiddle, leftWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);
        MondrianSparkPartition rhs = new MondrianSparkPartition(subPartitions._2._1(), rightMiddle, rightWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);

        return Arrays.asList(lhs, rhs);
    }

    private List<Interval> copyList(List<Interval> w) {
        List<Interval> newList = new ArrayList<>(w.size());
        for (Interval interval : w) {
            if (interval == null) {
                newList.add(null);
            } else {
                newList.add(interval.clone());
            }
        }
        return newList;
    }

    private List<MondrianSparkPartition> splitCategorical(int quasiIndex, int level) {
        switch (categoricalSplitStrategy) {
            case ORDER_BASED:
                return splitCategoricalOrderBased(quasiIndex, level);
            case HIERARCHY_BASED:
            default:
                return splitCategoricalHierarchyBased(quasiIndex);
        }
    }

    private List<MondrianSparkPartition> splitCategoricalOrderBased(int quasiIndex, int level) {
        int columnIndex = this.quasiColumns.get(quasiIndex);
        MaterializedHierarchy materializedHierarchy = (MaterializedHierarchy)
                ((CategoricalInformation) this.columnInformationList.get(columnIndex)).getHierarchy();

        Dataset<Double> categoricalIndices = MondrianSparkUtils.extractCategoricalIndices(this.member, columnIndex, materializedHierarchy);
        categoricalIndices = categoricalIndices.sort(org.apache.spark.sql.functions.col("value").asc());

        double median = MondrianSparkUtils.findMedian(categoricalIndices)._3();

        Tuple2<Tuple4<Dataset<String[]>, Double, Double, Double>, Tuple4<Dataset<String[]>, Double, Double, Double>> subPartitions =
                MondrianSparkUtils.splitCategoricalByOrder(this.member, columnIndex, median, materializedHierarchy);

        if (subPartitions._1._1().isEmpty() || subPartitions._2._1().isEmpty()) {
            return Collections.emptyList();
        }

        if (!checkPartitionForConstraints(subPartitions._1._1()) || !checkPartitionForConstraints(subPartitions._2._1())) {
            return Collections.emptyList();
        }

        double leftMin = subPartitions._1._2();
        double leftMax = subPartitions._1._3();

        double rightMin = subPartitions._2._2();
        double rightMax = subPartitions._2._3();

        List<String> leftMiddle = new ArrayList<>(middle);
        leftMiddle.set(columnIndex, null);

        List<String> rightMiddle = new ArrayList<>(middle);
        rightMiddle.set(columnIndex, null);

        List<Interval> leftWidth = copyList(width);
        leftWidth.set(columnIndex, new Interval(leftMin, leftMax));

        List<Interval> rightWidth = copyList(width);
        rightWidth.set(columnIndex, new Interval(rightMin, rightMax));

        MondrianSparkPartition lhs = new MondrianSparkPartition(subPartitions._1._1(), leftMiddle, leftWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);
        MondrianSparkPartition rhs = new MondrianSparkPartition(subPartitions._2._1(), rightMiddle, rightWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);

        return Arrays.asList(lhs, rhs);
    }

    private List<MondrianSparkPartition> splitCategoricalHierarchyBased(int quasiIndex) {
        throw new RuntimeException("not implemented yet");
    }

    public List<MondrianSparkPartition> split(int quasiIndex, int level) {
        ColumnInformation columnInformation = columnInformationList.get(this.quasiColumns.get(quasiIndex));

        if (!columnInformation.isCategorical()) {
            return splitNumerical(quasiIndex, level);
        } else {
            return splitCategorical(quasiIndex, level);
        }
    }

    public double getNormalizedWidth(int columnIndex) {
        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        double dividend;

        if (columnInformation.isCategorical()) {
            CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
            String topTerm = categoricalInformation.getHierarchy().getTopTerm();
            dividend = ((MaterializedHierarchy) categoricalInformation.getHierarchy()).getNode(topTerm).length();
        } else {
            NumericalRange numericalInformation = (NumericalRange) columnInformation;
            dividend = numericalInformation.getRange();
        }

        return width.get(columnIndex).getRange() / dividend;
    }

    public int chooseDimension() {
        int maxWidth = -1;
        int maxDim = -1;

        for (int i = 0; i < qiLen; i++) {
            if (allow[i] == 0) {
                continue;
            }

            int normalizedWidth = (int) getNormalizedWidth(this.quasiColumns.get(i));
            if (normalizedWidth > maxWidth) {
                maxWidth = normalizedWidth;
                maxDim = i;
            }
        }

        if (maxWidth > 1) {
            throw new RuntimeException("maxWidth > 1");
        }

        if (maxDim == -1) {
            throw new RuntimeException("maxDim == -1");
        }

        return maxDim;
    }

    public MondrianSparkPartition(Dataset<String[]> data, List<String> middle, List<Interval> width,
                                  List<ColumnInformation> columnInformationList,
                                  List<PrivacyConstraint> privacyConstraints, CategoricalSplitStrategy categoricalSplitStrategy) {
        this.member = data;
        this.middle = middle;
        this.width = width;
        this.quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        this.nonQuasiColumns = Mondrian.getNonQuasiColumns(columnInformationList.size(), this.quasiColumns);
        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.SENSITIVE);

        this.qiLen = quasiColumns.size();
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.categoricalSplitStrategy = categoricalSplitStrategy;
        this.privacyConstraintsContentRequirements = AnonymizationUtils.buildPrivacyConstraintContentRequirements(privacyConstraints);

        this.allow = new int[qiLen];
        Arrays.fill(allow, 1);

        this.privacyMetrics = new ArrayList<>();
        for (PrivacyConstraint constraint : privacyConstraints) {
            this.privacyMetrics.add(constraint.getMetricInstance());
        }
    }
}
