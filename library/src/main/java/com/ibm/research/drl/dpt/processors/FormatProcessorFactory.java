/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
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
