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
package com.ibm.research.drl.dpt.processors;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;

public class FormatProcessorFactory {

    public static FormatProcessor getProcessor(DataTypeFormat datatypeFormat) {

        switch (datatypeFormat) {
            case CSV:
                return new CSVFormatProcessor();
            case JSON:
                return new JSONFormatProcessor();
//            case PARQUET:
//                return new ParquetFormatProcessor();
//            case DICOM:
//                return new DICOMFormatProcessor();
            case XLSX:
            case XLS:
                return new ExcelFormatProcessor();
            case XML:
                return new XMLFormatProcessor();
            case HL7:
                return new HL7FormatProcessor();
            case FHIR_JSON:
                return new FHIRJsonFormatProcessor();
//            case JDBC:
//                return new JDBCFormatProcessor();
            default:
                throw new IllegalArgumentException("Unsupported data input format type");
        }
    }

}
