/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

public enum ColumnType {
    /**
     * Direct identifier column type.
     */
    DIRECT_IDENTIFIER,
    /**
     * Quasi column type.
     */
    QUASI,
    /**
     * Epsilon-Quasi column type.
     */
    E_QUASI,
    /**
     * Normal column type.
     */
    NORMAL,
    /**
     * Sensitive column type.
     */
    SENSITIVE
}

