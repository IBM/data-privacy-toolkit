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
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientIDIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        PatientIDIdentifier identifier = new PatientIDIdentifier();

        String[] validIDs = {"553-455-222-566"};
        for (String id: validIDs) {
            assertTrue(identifier.isOfThisType(id));
        }

        String[] invalidIDs = {"55A-455-222-566", "555-444-333"};
        for (String id: invalidIDs) {
            assertFalse(identifier.isOfThisType(id));
        }
    }
}
