/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.schema;

public enum RelationshipType {
    /**
     * Sum relationship type.
     */
    SUM,
    /**
     * Sum approximate relationship type.
     */
    SUM_APPROXIMATE,
    /**
     * Product relationship type.
     */
    PRODUCT,
    /**
     * Equals relationship type.
     */
    EQUALS,
    /**
     * Greater relationship type.
     */
    GREATER,
    /* For dates */
    DISTANCE,
    /**
     * Less relationship type.
     */
    LESS,
    /**
     * Linked relationship type.
     */
    LINKED,

    KEY,

    RATIO,

    GREP_AND_MASK /* for freetext */
}
