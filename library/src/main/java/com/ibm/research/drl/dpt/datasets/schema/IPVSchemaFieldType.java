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
package com.ibm.research.drl.dpt.datasets.schema;

/**
 * The enum Ipv schema field type.
 *
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
    TIMESTAMP
}
