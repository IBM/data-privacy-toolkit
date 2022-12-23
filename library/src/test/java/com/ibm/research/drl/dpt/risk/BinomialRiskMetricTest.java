/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.junit.jupiter.api.Test;


public class BinomialRiskMetricTest {
    
    
    @Test
    public void testPrintFK() {
        double N = 8000000;
        double pi_k = 0.6;
        
        PoissonDistribution poissonDistribution = new PoissonDistribution(N * pi_k);

        System.out.println(poissonDistribution.sample());
    }
    

}
