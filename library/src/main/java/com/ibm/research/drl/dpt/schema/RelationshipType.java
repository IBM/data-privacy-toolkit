/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.schema;

public enum RelationshipType {
    SUM,
    SUM_APPROXIMATE,
    PRODUCT,
    EQUALS,
    GREATER,
    DISTANCE,
    LESS,
    LINKED,
    KEY,
    RATIO,
    GREP_AND_MASK /* for freetext */
}
