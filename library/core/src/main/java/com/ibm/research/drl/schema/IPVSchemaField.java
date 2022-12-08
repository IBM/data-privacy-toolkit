/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
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
