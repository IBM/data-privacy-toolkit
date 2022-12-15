/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public enum ValueClass {
    /**
     * Numeric value class.
     */
    NUMERIC,
    /**
     * Date value class.
     */
    DATE,
    /**
     * Location value class.
     */
    LOCATION,
    /**
     * Text value class.
     */
    TEXT,
    /**
     * Unknown value class.
     */
    UNKNOWN
}
