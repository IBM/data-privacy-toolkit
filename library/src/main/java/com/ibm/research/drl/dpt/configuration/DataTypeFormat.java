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
package com.ibm.research.drl.dpt.configuration;

/**
 * Enumerates the data type formats supported by the toolkit.
 */
public enum DataTypeFormat {
    /** Comma-Separated Values format. */
    CSV,
    /** JSON format. */
    JSON,
    /** DICOM medical imaging format. */
    DICOM,
    /** Microsoft Excel 97-2003 format. */
    XLS,
    /** Microsoft Excel Open XML format. */
    XLSX,
    /** XML format. */
    XML,
    /** PDF format. */
    PDF,
    /** Microsoft Word 97-2003 format. */
    DOC,
    /** Microsoft Word Open XML format. */
    DOCX,
    /** Plain text format. */
    PLAIN,
    /** HL7 FHIR JSON format. */
    FHIR_JSON,
    /** HL7 v2 format. */
    HL7,
    /** Apache Parquet format. */
    PARQUET,
    /** Variant Call Format. */
    VCF,
    /** JDBC (database) format. */
    JDBC
}

