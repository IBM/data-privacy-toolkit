/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.processors.records;


/**
 * Interface representing a single data record with named field access.
 */
public interface Record {

    /**
     * Returns whether this record is a header record.
     *
     * @return true if this is a header record
     */
    boolean isHeader();

    /**
     * Returns the field references (names or paths) available in this record.
     *
     * @return an iterable of field reference strings
     */
    Iterable<String> getFieldReferences();

    /**
     * Returns the raw bytes of the specified field.
     *
     * @param fieldReference the field reference
     * @return the field value as a byte array
     */
    byte[] getFieldValue(String fieldReference);

    /**
     * Suppresses (removes or blanks) the specified field.
     *
     * @param field the field reference to suppress
     */
    void suppressField(String field);

    /**
     * Sets the value of the specified field.
     *
     * @param fieldReference the field reference
     * @param value          the new value as a byte array
     */
    void setFieldValue(String fieldReference, byte[] value);

    @Override
    String toString();

    /**
     * Returns this record serialized to bytes.
     *
     * @return the byte representation of this record
     */
    byte[] toBytes();
}
