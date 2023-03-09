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
package com.ibm.research.drl.dpt.anonymization.kmeans;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KMeansClusterTest {
    
    @Test
    public void testComputeCenter() {
        KMeansCluster cluster = new KMeansCluster();
        
        cluster.add(Arrays.asList(0.0, 0.0, 1.0));
        cluster.add(Arrays.asList(1.0, 1.0, 2.0));
       
        cluster.computeCenter();
        
        List<Double> center = cluster.getCenter();
       
        assertEquals(3, center.size());
        assertEquals(0.5, center.get(0), 0.00001);
        assertEquals(0.5, center.get(1), 0.00001);
        assertEquals(1.5, center.get(2), 0.00001);
    }
}

