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

class ItalianFiscalCodeIdentifierTest {
    @Test
    public void checkValid() {
        Identifier identifier = new ItalianFiscalCodeIdentifier();

        for (String valid : new String[]{
                "BRGSFN81P10L682K",
                "MRTMTT25D09F205Z",
                "MLLSNT82P65Z404U",
        }) {
            assertTrue(identifier.isOfThisType(valid), valid);
        }
    }

    @Test
    public void testInvalid() {
        Identifier identifier = new ItalianFiscalCodeIdentifier();

        for (String invalid : new String[]{
                "MLLSNT82P65Z4049",
                "MLLSNT82P65Z404D",
                "MLLSNT82P65Z404J",
                "MLL-NT8-P65Z404U",
                "MLLSNT82P65ZJDS404U",
                "MLLSNT8204U"
        }) {
            assertFalse(identifier.isOfThisType(invalid), invalid);
        }
    }
}