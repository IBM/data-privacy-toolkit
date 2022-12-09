/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractComplexMaskingProviderTest {

    private class DummyComplexMaskingProvider extends AbstractComplexMaskingProvider<String> {

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


