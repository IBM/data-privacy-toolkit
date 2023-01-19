/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

public enum Mechanism {
    BINARY,
    CATEGORICAL,
    LAPLACE_NATIVE,
    LAPLACE_BOUNDED,
    LAPLACE_TRUNCATED,
    GEOMETRIC_NATIVE,
    GEOMETRIC_TRUNCATED
}

