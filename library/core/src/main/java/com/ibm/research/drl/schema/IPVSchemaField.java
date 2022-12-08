/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.schema;

/**
 * The interface Ipv schema field.
 *
 * @author stefanob
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
