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

