/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;


import java.util.List;

public interface PrivacyMetric {
    PrivacyMetric getInstance(List<String> sensitiveValues);

    void update(PrivacyMetric metric);
}
