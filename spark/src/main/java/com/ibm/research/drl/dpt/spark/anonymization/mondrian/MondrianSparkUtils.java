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
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;


public class MondrianSparkUtils {

    public static JavaRDD<Double> extractValues(JavaRDD<String[]> input, int columnIndex, boolean distinct) {
        JavaRDD<Double> result = input.map(s -> Double.parseDouble(s[columnIndex]));
        if (!distinct) {
            return result;
        }

        return result.distinct();
    }

    public static Tuple3<Double, Double, Double> findMedian(JavaRDD<Double> input) {
        JavaPairRDD<Long, Double> sortedRDD = input.sortBy(aDouble -> aDouble, true, input.getNumPartitions()).zipWithIndex()
                .mapToPair(s -> new Tuple2<>(s._2, s._1)).cache();

        long n = sortedRDD.count();

        if (n == 0) {
            throw new RuntimeException("empty RDD");
        }

        double median;

        if (n % 2 == 0) {
            long l = n / 2 - 1;
            long r = l + 1;
            median = (sortedRDD.lookup(l).get(0) + sortedRDD.lookup(r).get(0)) / 2.0;
        } else {
            median = sortedRDD.lookup(n / 2).get(0);
        }

        double low = sortedRDD.lookup(0L).get(0);
        double high = sortedRDD.lookup(n - 1).get(0);

        sortedRDD.unpersist();
        
        return new Tuple3<>(low, high, median);

    }
    
    public static Integer getIndexForValue(String v, MaterializedHierarchy materializedHierarchy) {
        Integer index = materializedHierarchy.getIndex(v);
        if (index == null) {
            index = -1;
        }

        return index;
    }


    public static JavaRDD<Double> extractCategoricalIndices(JavaRDD<String[]> input, int columnIndex, MaterializedHierarchy materializedHierarchy) {
        return input.map(s -> {
            String v = s[columnIndex];
            return (double)getIndexForValue(v, materializedHierarchy);
        }).distinct();

    }
    
    public static Tuple2<Tuple4<JavaRDD<String[]>, Double, Double, Double>, Tuple4<JavaRDD<String[]>, Double, Double, Double>>
    splitCategoricalByOrder(final JavaRDD<String[]> input, final int columnIndex, final double median, final MaterializedHierarchy materializedHierarchy) {

        JavaPairRDD<String[], Double> valuesWithIndices = input.mapToPair(s -> {
            String v = s[columnIndex];
            double value = getIndexForValue(v, materializedHierarchy);

            return new Tuple2<>(s, value);
        });
                
        JavaPairRDD<String[], Double> left = valuesWithIndices.filter(s -> (s._2 < median));

        Tuple4<JavaRDD<String[]>, Double, Double, Double> lhs;
        
        if (!left.isEmpty()) {
            Tuple3<Double, Double, Double> leftPartitionInfo = 
                    MondrianSparkUtils.findMedian(left.values().distinct());
            lhs = new Tuple4<>(left.keys(), leftPartitionInfo._1(), leftPartitionInfo._2(), leftPartitionInfo._3());
        }
        else {
            lhs = new Tuple4<>(left.keys(), null, null, null);
        }

        JavaPairRDD<String[], Double> right = valuesWithIndices.filter(s -> !(s._2 < median));

        Tuple4<JavaRDD<String[]>, Double, Double, Double> rhs;
        
        if (!right.isEmpty()) {
            Tuple3<Double, Double, Double> rightPartitionInfo = MondrianSparkUtils.findMedian(right.values().distinct());
            rhs = new Tuple4<>(right.keys(), rightPartitionInfo._1(), rightPartitionInfo._2(), rightPartitionInfo._3());
        }
        else {
            rhs = new Tuple4<>(right.keys(), null, null, null);
        }
        
        return new Tuple2<>(lhs, rhs);
    }
    
    public static Tuple2<Tuple4<JavaRDD<String[]>, Double, Double, Double>, Tuple4<JavaRDD<String[]>, Double, Double, Double>> 
                splitNumericalByMedian(final JavaRDD<String[]> input, final int columnIndex, final double median) {
     
        long start = System.currentTimeMillis();
        JavaRDD<String[]> left = input.filter(s -> {
            double value = Double.parseDouble(s[columnIndex]);
            return (value < median);
        });

        System.out.println("\tleft filter took: " + (System.currentTimeMillis() - start));
        
        Tuple4<JavaRDD<String[]>, Double, Double, Double> lhs;

        start = System.currentTimeMillis();
        if (!left.isEmpty()) {
            Tuple3<Double, Double, Double> leftPartitionInfo = MondrianSparkUtils.findMedian(MondrianSparkUtils.extractValues(left, columnIndex, false));
            lhs = new Tuple4<>(left, leftPartitionInfo._1(), leftPartitionInfo._2(), leftPartitionInfo._3());
        }
        else {
            lhs = new Tuple4<>(left, null, null, null);
        }
        
        System.out.println("\tfind median took: " + (System.currentTimeMillis() - start));

        JavaRDD<String[]> right = input.filter(s -> {
            double value = Double.parseDouble(s[columnIndex]);
            return !(value < median);
        });

        Tuple4<JavaRDD<String[]>, Double, Double, Double> rhs;
       
        if (!right.isEmpty()) {
            Tuple3<Double, Double, Double> rightPartitionInfo = MondrianSparkUtils.findMedian(MondrianSparkUtils.extractValues(right, columnIndex, false));
            rhs = new Tuple4<>(right, rightPartitionInfo._1(), rightPartitionInfo._2(), rightPartitionInfo._3());
        }
        else  {
            rhs = new Tuple4<>(right, null, null, null);
        }
       
        return new Tuple2<>(lhs, rhs);
    }

    public static double calculateCardinality(JavaRDD<String[]> inputRDD, int columnIndex) {
        return inputRDD.map(s -> s[columnIndex]).distinct().count();
    }
    
}
