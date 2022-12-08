/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRDevice;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRModelTest {

    @Test
    public void testSerialization() throws Exception{
        InputStream inputStream = this.getClass().getResourceAsStream("/fhir/deviceExample.json");

        ObjectMapper objectMapper = new ObjectMapper();

        FHIRDevice device = objectMapper.readValue(inputStream, FHIRDevice.class);

        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, device);

        String jsonBack = stringWriter.toString();

        System.out.println(jsonBack);

        JsonNode node = objectMapper.readTree(jsonBack);
        assertEquals("device", node.get("resourceType").asText().toLowerCase());
    }
}
