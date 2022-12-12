/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;
import java.util.Map;

public interface RiskMetric {
    String getName();
    String getShortName();
    double report();
    RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options);
    void validateOptions(Map<String, String> options) throws IllegalArgumentException;
}
