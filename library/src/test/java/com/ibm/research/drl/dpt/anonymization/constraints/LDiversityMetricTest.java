/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;


import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;
import com.ibm.research.drl.dpt.util.Histogram;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LDiversityMetricTest {

    @Test
    public void testGetInstance() {
        LDiversityMetric metric = new LDiversityMetric();
        assertEquals(0, metric.getCount());

        List<String> sensitiveValues = new ArrayList<>();
        sensitiveValues.add("a");
        sensitiveValues.add("b");

        PrivacyMetric instance = metric.getInstance(sensitiveValues);
        LDiversityMetric other = (LDiversityMetric)instance;
        assertEquals(1, other.getCount());

        assertEquals(2, other.getHistograms().size());

        Histogram<String> h0 = other.getHistograms().get(0);
        Histogram<String> h1 = other.getHistograms().get(1);

        assertEquals(1, h0.size());
        assertEquals(1, h1.size());

        assertEquals(1L, h0.get("a").longValue());
        assertEquals(1L, h1.get("b").longValue());
    }

    @Test
    public void testUpdate() {
        LDiversityMetric metric = new LDiversityMetric();
        assertEquals(0, metric.getCount());

        List<String> sensitiveValues = new ArrayList<>();
        sensitiveValues.add("a");
        sensitiveValues.add("b");

        LDiversityMetric other = new LDiversityMetric(sensitiveValues);

        metric.update(other);
        assertEquals(1, metric.getCount());

        assertEquals(2, metric.getHistograms().size());

        Histogram<String> h0 = metric.getHistograms().get(0);
        Histogram<String> h1 = metric.getHistograms().get(1);

        assertEquals(1, h0.size());
        assertEquals(1, h1.size());

        assertEquals(1L, h0.get("a").longValue());
        assertEquals(1L, h1.get("b").longValue());

        metric.update(other);
        assertEquals(2, metric.getCount());
    }
}
