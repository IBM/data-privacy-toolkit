/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.datasets.schema;

/**
 * The interface Ipv schema field.
 *
 */
public interface IPVSchemaField {
    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets type.
     *
     * @return the type
     */
    IPVSchemaFieldType getType();
}
