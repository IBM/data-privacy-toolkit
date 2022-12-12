/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.util.Collection;

public interface IPVAlgorithm {
    Collection<IPVVulnerability> apply(IPVDataset dataset);
}
