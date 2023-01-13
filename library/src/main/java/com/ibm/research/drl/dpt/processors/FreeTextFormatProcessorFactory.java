/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;

public class FreeTextFormatProcessorFactory {
    public static FreeTextFormatProcessor getProcessor(DataTypeFormat datatypeFormat) {

        switch (datatypeFormat) {
            case PLAIN:
                return new PlainFreeTextFormatProcessor();
            case DOCX:
                return new DOCXFreeTextFormatProcessor();
            case PDF:
                return new PDFFreeTextFormatProcessor();
            default:
                throw new RuntimeException("Not supported yet");
        }
    }
}