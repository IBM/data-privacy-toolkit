package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

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