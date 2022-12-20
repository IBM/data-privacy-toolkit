/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DistributionExtractorTest {
    @Test
    public void testCalculateCDF() {
        List<Tuple<Double, Double>> numbers = new ArrayList<>();
        numbers.add(new Tuple<>(1.0, 10.0));
        numbers.add(new Tuple<>(2.0, 5.0));
        numbers.add(new Tuple<>(3.0, 25.0));
        numbers.add(new Tuple<>(4.0, 30.0));
        numbers.add(new Tuple<>(5.0, 30.0));

        List<Tuple<Double, Double>> cdf = DistributionExtractor.calculateCDF(numbers);
        
        assertEquals(cdf.size(), numbers.size());
        assertEquals(1.0, cdf.get(0).getFirst(), 0.0);
        assertEquals(0.1, cdf.get(0).getSecond(), 0.0);

        assertEquals(2.0, cdf.get(1).getFirst(), 0.0);
        assertEquals(0.15, cdf.get(1).getSecond(), 0.0);
        
        assertEquals(3.0, cdf.get(2).getFirst(), 0.0);
        assertEquals(0.4, cdf.get(2).getSecond(), 0.0);
        
        assertEquals(4.0, cdf.get(3).getFirst(), 0.0);
        assertEquals(0.7, cdf.get(3).getSecond(), 0.0);
        
        assertEquals(5.0, cdf.get(4).getFirst(), 0.0);
        assertEquals(1.0, cdf.get(4).getSecond(), 0.0);
    }
}