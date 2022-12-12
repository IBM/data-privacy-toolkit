/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

public interface AnonymityChecker {
    double calculateSuppressionRate(LatticeNode node);
}

