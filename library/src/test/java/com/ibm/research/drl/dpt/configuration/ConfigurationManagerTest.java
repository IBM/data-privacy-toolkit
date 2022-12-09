/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationManagerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void loadStream() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);
        }
    }

    @Test
    public void getFieldConfiguration() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);

            MaskingConfiguration configuration = manager.getFieldConfiguration("field2");

            assertNotNull(configuration);

            assertThat(configuration.getBooleanValue("phone.countryCode.preserve"), is(true));
        }
    }

    @Test
    public void getDefaultConfiguration() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/test_configuration.json")) {
            ConfigurationManager manager = ConfigurationManager.load(mapper.readTree(is));

            assertNotNull(manager);

            MaskingConfiguration configuration = manager.getDefaultConfiguration();

            assertNotNull(configuration);

            assertThat(configuration.getBooleanValue("phone.countryCode.preserve"), is(false));
            
            assertEquals(JsonNodeType.OBJECT, configuration.getJsonNodeValue("generalization.masking.hierarchyMap").getNodeType());
        }
    }
}