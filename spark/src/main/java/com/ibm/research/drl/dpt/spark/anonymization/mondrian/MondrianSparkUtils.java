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

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.util.List;


public class MondrianSparkUtils {

    public static Dataset<Double> extractValues(Dataset<String[]> input, int columnIndex, boolean distinct) {
        Dataset<Double> result = input.map(
                (MapFunction<String[], Double>) s -> Double.parseDouble(s[columnIndex]),
                Encoders.DOUBLE());
        if (!distinct) {
            return result;
        }
        return result.distinct();
    }

    public static Tuple3<Double, Double, Double> findMedian(Dataset<Double> input) {
        List<Double> sorted = input.sort(org.apache.spark.sql.functions.col("value").asc()).collectAsList();
        long n = sorted.size();

        if (n == 0) {
            throw new RuntimeException("empty Dataset");
        }

        double median;
        if (n % 2 == 0) {
            median = (sorted.get((int)(n / 2 - 1)) + sorted.get((int)(n / 2))) / 2.0;
        } else {
            median = sorted.get((int)(n / 2));
        }

        double low = sorted.get(0);
        double high = sorted.get((int)(n - 1));

        return new Tuple3<>(low, high, median);
    }

    public static Integer getIndexForValue(String v, MaterializedHierarchy materializedHierarchy) {
        Integer index = materializedHierarchy.getIndex(v);
        if (index == null) {
            index = -1;
        }
        return index;
    }

    public static Dataset<Double> extractCategoricalIndices(Dataset<String[]> input, int columnIndex, MaterializedHierarchy materializedHierarchy) {
        return input.map(
                (MapFunction<String[], Double>) s -> (double) getIndexForValue(s[columnIndex], materializedHierarchy),
                Encoders.DOUBLE()
        ).distinct();
    }

    public static Tuple2<Tuple4<Dataset<String[]>, Double, Double, Double>, Tuple4<Dataset<String[]>, Double, Double, Double>>
    splitCategoricalByOrder(final Dataset<String[]> input, final int columnIndex, final double median, final MaterializedHierarchy materializedHierarchy) {

        Dataset<String[]> left = input.filter(
                (FilterFunction<String[]>) s -> (double) getIndexForValue(s[columnIndex], materializedHierarchy) < median);

        Tuple4<Dataset<String[]>, Double, Double, Double> lhs;
        if (!left.isEmpty()) {
            Tuple3<Double, Double, Double> leftPartitionInfo =
                    MondrianSparkUtils.findMedian(left.map(
                            (MapFunction<String[], Double>) s -> (double) getIndexForValue(s[columnIndex], materializedHierarchy),
                            Encoders.DOUBLE()).distinct());
            lhs = new Tuple4<>(left, leftPartitionInfo._1(), leftPartitionInfo._2(), leftPartitionInfo._3());
        } else {
            lhs = new Tuple4<>(left, null, null, null);
        }

        Dataset<String[]> right = input.filter(
                (FilterFunction<String[]>) s -> !((double) getIndexForValue(s[columnIndex], materializedHierarchy) < median));

        Tuple4<Dataset<String[]>, Double, Double, Double> rhs;
        if (!right.isEmpty()) {
            Tuple3<Double, Double, Double> rightPartitionInfo = MondrianSparkUtils.findMedian(right.map(
                    (MapFunction<String[], Double>) s -> (double) getIndexForValue(s[columnIndex], materializedHierarchy),
                    Encoders.DOUBLE()).distinct());
            rhs = new Tuple4<>(right, rightPartitionInfo._1(), rightPartitionInfo._2(), rightPartitionInfo._3());
        } else {
            rhs = new Tuple4<>(right, null, null, null);
        }

        return new Tuple2<>(lhs, rhs);
    }

    public static Tuple2<Tuple4<Dataset<String[]>, Double, Double, Double>, Tuple4<Dataset<String[]>, Double, Double, Double>>
    splitNumericalByMedian(final Dataset<String[]> input, final int columnIndex, final double median) {

        long start = System.currentTimeMillis();
        Dataset<String[]> left = input.filter(
                (FilterFunction<String[]>) s -> Double.parseDouble(s[columnIndex]) < median);

        System.out.println("\tleft filter took: " + (System.currentTimeMillis() - start));

        Tuple4<Dataset<String[]>, Double, Double, Double> lhs;
        start = System.currentTimeMillis();
        if (!left.isEmpty()) {
            Tuple3<Double, Double, Double> leftPartitionInfo = MondrianSparkUtils.findMedian(MondrianSparkUtils.extractValues(left, columnIndex, false));
            lhs = new Tuple4<>(left, leftPartitionInfo._1(), leftPartitionInfo._2(), leftPartitionInfo._3());
        } else {
            lhs = new Tuple4<>(left, null, null, null);
        }

        System.out.println("\tfind median took: " + (System.currentTimeMillis() - start));

        Dataset<String[]> right = input.filter(
                (FilterFunction<String[]>) s -> !(Double.parseDouble(s[columnIndex]) < median));

        Tuple4<Dataset<String[]>, Double, Double, Double> rhs;
        if (!right.isEmpty()) {
            Tuple3<Double, Double, Double> rightPartitionInfo = MondrianSparkUtils.findMedian(MondrianSparkUtils.extractValues(right, columnIndex, false));
            rhs = new Tuple4<>(right, rightPartitionInfo._1(), rightPartitionInfo._2(), rightPartitionInfo._3());
        } else {
            rhs = new Tuple4<>(right, null, null, null);
        }

        return new Tuple2<>(lhs, rhs);
    }

    public static double calculateCardinality(Dataset<String[]> inputRDD, int columnIndex) {
        return inputRDD.map(
                (MapFunction<String[], String>) s -> s[columnIndex],
                Encoders.STRING()).distinct().count();
    }
}
