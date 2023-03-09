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

public class SSNUKIdentifierTest {

    @Test
    public void testIsOfThisType() {
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";
        assertTrue(identifier.isOfThisType(ssn));

        //ignores spaces
        ssn = "AB 12 34 56 C";
        assertTrue(identifier.isOfThisType(ssn));

        //check for not allowed characters
        ssn = "DB123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "AD123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "AO123456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA12A456C";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA1234567";
        assertFalse(identifier.isOfThisType(ssn));
        ssn = "BA123456Z";
        assertFalse(identifier.isOfThisType(ssn));

        //'O' is allowed on the first character
        ssn = "OA123456C";
        assertTrue(identifier.isOfThisType(ssn));
    }
}
