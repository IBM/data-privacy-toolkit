/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class IdentificationConfigurationTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testFromNode() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/validIdentificationConfiguration.json")) {

            IdentificationConfiguration identificationConfiguration = mapper.readValue(inputStream, IdentificationConfiguration.class);

            assertEquals(50, identificationConfiguration.getPriorityForType("foobar"));
            assertEquals(90, identificationConfiguration.getPriorityForType("EMAIL"));

            assertFalse(identificationConfiguration.getConsiderEmptyForFrequency());
            assertEquals(IdentificationStrategy.FREQUENCY_BASED, identificationConfiguration.getIdentificationStrategy());
        }
    }

}