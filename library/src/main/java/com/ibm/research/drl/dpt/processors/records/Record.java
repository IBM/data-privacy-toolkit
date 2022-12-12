/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors.records;


public interface Record {

    boolean isHeader();

    Iterable<String> getFieldReferences();
    byte[] getFieldValue(String fieldReference);

    void suppressField(String field);
    void setFieldValue(String fieldReference, byte[] value);

    String toString();
    byte[] toBytes();
}
