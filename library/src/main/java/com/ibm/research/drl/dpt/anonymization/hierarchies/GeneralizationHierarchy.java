/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;


import java.io.Serializable;
import java.util.Set;


public interface GeneralizationHierarchy extends Serializable {
    int getHeight();

    long getTotalLeaves();

    int leavesForNode(String value);

    Set<String> getNodeLeaves(String value);

    int getNodeLevel(String value);

    String getTopTerm();

    String encode(String value, int level, boolean randomizeOnFail);
}
