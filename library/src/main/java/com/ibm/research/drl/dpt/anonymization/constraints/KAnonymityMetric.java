/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;

import java.io.Serializable;
import java.util.List;

public class KAnonymityMetric implements PrivacyMetric, Serializable {
    private long count = 0L;

    public long getCount() {
        return count;
    }

    public KAnonymityMetric() {
    }

    public KAnonymityMetric(long cnt) {
        this.count = cnt;
    }

    @Override
    public PrivacyMetric getInstance(List<String> sensitiveValues) {
        return new KAnonymityMetric(1);
    }

    @Override
    public void update(PrivacyMetric metric) {
        count += ((KAnonymityMetric) metric).getCount();
    }
}

