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
 * The enum Ipv schema field type.
 *
 * @author stefanob
 */
public enum IPVSchemaFieldType {
    /**
     * Attachment blob ipv schema field type.
     */
    ATTACHMENT_BLOB,
    /**
     * Attachment csv ipv schema field type.
     */
    ATTACHMENT_CSV,
    /**
     * Attachment json blob ipv schema field type.
     */
    ATTACHMENT_JSON_BLOB,
    /**
     * Attachment json table ipv schema field type.
     */
    ATTACHMENT_JSON_TABLE,
    /**
     * Boolean ipv schema field type.
     */
    BOOLEAN,
    /**
     * Calendar date ipv schema field type.
     */
    CALENDAR_DATE,
    /**
     * Float ipv schema field type.
     */
    FLOAT,
    /**
     * Inline json blob ipv schema field type.
     */
    INLINE_JSON_BLOB,
    /**
     * Int ipv schema field type.
     */
    INT,
    /**
     * String ipv schema field type.
     */
    STRING,
    /**
     * Timestamp ipv schema field type.
     */
    TIMESTAMP;
}
