/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRHumanName;
import com.ibm.research.drl.dpt.models.fhir.resources.FHIRPatient;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRPatientMaskingProviderTest {

    private String getFileContents(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");
        String line;
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();

    }

    @Test
    public void testMapping() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/fhir/patientExample.json");
        String contents = getFileContents(inputStream);

        ObjectMapper objectMapper = new ObjectMapper();

        FHIRPatient fhirPatient = objectMapper.readValue(contents, FHIRPatient.class);

        Collection<FHIRHumanName> names = fhirPatient.getName();

        assertEquals(2, names.size());

        FHIRHumanName name1 = names.iterator().next();
        assertEquals("official", name1.getUse());
        assertEquals(1, name1.getFamily().size());
        assertEquals("Chalmers", name1.getFamily().iterator().next());
        assertEquals(2, name1.getGiven().size());

        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, fhirPatient);

        String jsonString = stringWriter.toString();
        JsonNode node = objectMapper.readTree(jsonString);

        assertEquals("patient", node.get("resourceType").asText().toLowerCase());
    }


}


