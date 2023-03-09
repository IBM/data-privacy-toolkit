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
