/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FieldRelationshipTest {

    @Test
    public void testSerialization() throws IOException{

        String json = "{\"fieldName\": \"field1\", \"valueClass\": \"LOCATION\", \"relationshipType\": \"LINKED\", \"operands\": [ {\"name\": \"field2\", \"type\": \"CITY\"} ]}";

        ObjectMapper mapper = new ObjectMapper();
        FieldRelationship fieldRelationship = mapper.readValue(json, FieldRelationship.class);
    }
}

