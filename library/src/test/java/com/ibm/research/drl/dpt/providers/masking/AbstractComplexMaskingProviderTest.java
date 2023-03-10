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
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractComplexMaskingProviderTest {

    private static class DummyComplexMaskingProvider extends AbstractComplexMaskingProvider<String> {

        private final MaskingConfiguration fieldConfiguration;

        public DummyComplexMaskingProvider(MaskingConfiguration maskingConfiguration) {
            super("fhir", maskingConfiguration, new HashSet<>(), null);

            this.fieldConfiguration = getConfigurationForSubfield("fhir.location.identifier", maskingConfiguration);
        }

        public String mask(String obj) { return null; }

        public MaskingConfiguration getFieldConfigurationForTest() {
            return this.fieldConfiguration;
        }
    }


    @Test
    public void testGetSubfieldConfiguration() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/complexConfiguration.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(new ObjectMapper().readTree(is));

            assertEquals("RANDOM", configurationManager.getDefaultConfiguration().getStringValue("default.masking.provider"));

            DummyComplexMaskingProvider dummyComplexMaskingProvider = new DummyComplexMaskingProvider(configurationManager.getDefaultConfiguration());
            MaskingConfiguration fieldMaskingConfiguration = dummyComplexMaskingProvider.getFieldConfigurationForTest();

            assertEquals("CITY", fieldMaskingConfiguration.getStringValue("default.masking.provider"));
            assertEquals("DUMMY", fieldMaskingConfiguration.getStringValue("dummy.conf.value"));
        }
    }
}


