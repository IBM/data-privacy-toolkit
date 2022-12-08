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

import java.util.List;

/**
 * The interface Ipv schema.
 *
 * @author stefanob
 */
public interface IPVSchema {
    /**
     * Gets schema identifier.
     *
     * @return the schema identifier
     */
    String getSchemaIdentifier();

    /**
     * Gets fields.
     *
     * @return the fields
     */
    List<? extends IPVSchemaField> getFields();
}
