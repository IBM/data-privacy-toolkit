/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.datasets.schema;

import java.util.List;

/**
 * The interface Ipv schema.
 *
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
