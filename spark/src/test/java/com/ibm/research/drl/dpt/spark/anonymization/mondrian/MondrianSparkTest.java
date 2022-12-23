/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MondrianSparkTest {
    
    @Test
    public void testMedian() {

        JavaSparkContext sc = new JavaSparkContext("local[4]", "test");
        
        List<Double> values = Arrays.asList(7.0,  9.0,  13.0, 12.0,  16.0);
        JavaRDD<Double> rdd = sc.parallelize(values);
        
        assertEquals(12.0, MondrianSparkUtils.findMedian(rdd)._3(), 0.0001);

        values = Arrays.asList(2.7,  3.5,  5.1,  8.3);
        assertEquals(4.3, MondrianSparkUtils.findMedian(sc.parallelize(values))._3(), 0.00001);

        assertEquals(1.0, MondrianSparkUtils.findMedian(sc.parallelize(Arrays.asList(1.0)))._3(), 0.000001);
    }
}
