/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.io.Serializable;

public interface ProbabilisticEntity extends  Serializable {
    double getProbability();
}
