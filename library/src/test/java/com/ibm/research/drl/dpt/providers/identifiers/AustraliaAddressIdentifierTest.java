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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AustraliaAddressIdentifierTest {
    @Test
    public void testRealWorldData() {
        String[] data = {
                "100 Flushcombe Road\n" +
                        "BLACKTOWN NSW 2148",

                "99 George Street\n" +
                        "PARRAMATTA NSW 2150",

                "PO Box 99\n" +
                        "PARRAMATTA NSW 2124"
        };

        Identifier identifier = new AustraliaAddressIdentifier();

        for (String value : data) {
            assertTrue(identifier.isOfThisType(value), value);
        }
    }

    @Test
    public void testWithRealWorldData() {
        String[] validAddresses = {
                "534 Erewhon St",
                "534 Erewhon St PeasantVille",
                "534 Erewhon St PeasantVille, Rainbow",
                "534 Erewhon St PeasantVille, Rainbow, Vic",
                "534 Erewhon St PeasantVille, Rainbow, Vic  3999",
        };

        Identifier identifier = new AustraliaAddressIdentifier();

        for (String validAddress : validAddresses) {
            assertTrue(identifier.isOfThisType(validAddress), validAddress);
        }
    }
}