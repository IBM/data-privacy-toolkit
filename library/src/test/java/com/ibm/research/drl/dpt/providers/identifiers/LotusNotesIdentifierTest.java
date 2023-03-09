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

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LotusNotesIdentifierTest {

    @Test
    public void identifiesValidId() {
        Identifier identifier = new LotusNotesIdentifier();

        for (String valid : Arrays.asList(
                "Name Surname/Country/Company",
                "Dario Gil/Organization/Company",
                "Stefano Braghin/Ireland/IBM@IBMIE"
        )) {
            assertTrue(identifier.isOfThisType(valid), valid);
        }
    }

    @Test
    public void correctlyExtractsPersonComponent() {
        IdentifierWithOffset identifier = new LotusNotesIdentifier();

        for (String[] valid : Arrays.asList(
                new String[]{"Name Surname/Country/Company", "Name Surname"},
                new String[]{"Dario Gil/Organization/Company", "Dario Gil"},
                new String[]{"Stefano Braghin/Ireland/IBM@IBMIE", "Stefano Braghin"}
        )) {
            Tuple<Boolean, Tuple<Integer, Integer>> match = identifier.isOfThisTypeWithOffset(valid[0]);
            assertTrue(match.getFirst(), valid[0]);
            String person = valid[0].substring(match.getSecond().getFirst(), match.getSecond().getFirst() + match.getSecond().getSecond());

            assertEquals(person, valid[1]);
        }
    }
}