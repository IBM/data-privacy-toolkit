/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
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

