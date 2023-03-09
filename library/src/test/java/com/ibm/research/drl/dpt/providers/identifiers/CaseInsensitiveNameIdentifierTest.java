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

class CaseInsensitiveNameIdentifierTest {
    @Test
    public void testPositive() throws Exception {
        CaseInsensitiveNameIdentifier identifier = new CaseInsensitiveNameIdentifier();

        assertTrue(identifier.isOfThisType("Carrol, John J."));
        assertTrue(identifier.isOfThisType("Carrol, John"));
        assertTrue(identifier.isOfThisType("Patrick K. Fitzgerald"));
        assertTrue(identifier.isOfThisType("Patrick Fitzgerald"));
        assertTrue(identifier.isOfThisType("Kennedy John"));

        assertTrue(identifier.isOfThisType("carrol, john j."));
        assertTrue(identifier.isOfThisType("carrol, john"));
        assertTrue(identifier.isOfThisType("patrick k. fitzgerald"));
        assertTrue(identifier.isOfThisType("patrick fitzgerald"));
        assertTrue(identifier.isOfThisType("kennedy john"));

        assertTrue(identifier.isOfThisType("giovanni"));

        assertTrue(identifier.isOfThisType("PaOLo"));

        assertTrue(identifier.isOfThisType("John E. Kelly"));
    }

    @Test
    public void testNegative() throws Exception {
        CaseInsensitiveNameIdentifier identifier = new CaseInsensitiveNameIdentifier();

        assertFalse(identifier.isOfThisType("12308u499234802"));
        assertFalse(identifier.isOfThisType("84032-43092-3242"));

        assertFalse(identifier.isOfThisType("32 Carter Avn."));
        assertFalse(identifier.isOfThisType("15 Kennedy Avenue"));
        assertFalse(identifier.isOfThisType("Thompson Avn 1000"));
        assertFalse(identifier.isOfThisType("10 20 33"));

        assertFalse(identifier.isOfThisType("il mio"));
    }
}