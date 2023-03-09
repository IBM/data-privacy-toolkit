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

public class NumericIdentifierTest {

    @Test
    public void testNumeric() {
        Identifier identifier = new NumericIdentifier();

        String[] validValues = {"123", "-1234", "+1234", "123.444", "-123.444"};
        for(String value: validValues) {
            assertTrue(identifier.isOfThisType(value));
        }

        String[] invalidValues = {"a123", "-1234a", "+1234a", "123a", "abcd", "", " "};
        for(String value: invalidValues) {
            assertFalse(identifier.isOfThisType(value));
        }

    }
}
