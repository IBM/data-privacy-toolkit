/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KAnonymityMetricTest {

    @Test
    public void testMetric() {
        KAnonymityMetric metric1 = new KAnonymityMetric();
        assertEquals(0L, metric1.getCount());

        metric1.update(new KAnonymityMetric());
        assertEquals(0L, metric1.getCount());

        metric1.update(new KAnonymityMetric(1));
        assertEquals(1L, metric1.getCount());
    }
}

