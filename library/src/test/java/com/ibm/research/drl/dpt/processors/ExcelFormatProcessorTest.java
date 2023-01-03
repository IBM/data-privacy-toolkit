/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class ExcelFormatProcessorTest {

    @Test
    public void testIdentifyStreamXLSX() throws Exception {
        try (InputStream inputStream = ExcelFormatProcessorTest.class.getResourceAsStream("/sampleXLS.xlsx")) {
            IdentificationReport results = new ExcelFormatProcessor().identifyTypesStream(inputStream, DataTypeFormat.XLSX, null, IdentifierFactory.defaultIdentifiers(), -1);

            assertThat(results.getRawResults().size(), greaterThan(0));
        }
    }

    @Test
    public void testIdentifyStreamXLS() throws Exception {
        try (InputStream inputStream = ExcelFormatProcessorTest.class.getResourceAsStream("/sampleXLS.xls")) {
            IdentificationReport results = new ExcelFormatProcessor().identifyTypesStream(inputStream, DataTypeFormat.XLS, null, IdentifierFactory.defaultIdentifiers(), -1);

            assertThat(results.getRawResults().size(), greaterThan(0));
        }
    }
}
