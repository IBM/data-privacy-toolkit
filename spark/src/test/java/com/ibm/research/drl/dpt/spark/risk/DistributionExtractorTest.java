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