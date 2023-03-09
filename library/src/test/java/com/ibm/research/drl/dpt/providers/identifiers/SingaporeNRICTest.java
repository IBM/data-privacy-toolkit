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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SingaporeNRICTest {
    @Test
    public void identifiesValidNumbers() {
        String[] validValues = {
                "S0000001I",
                "S0000002G",
                "S0000003E",
                "S0000004C",
                "S0000005A",
                "S0000006Z",
                "S0000007H",
        };

        Identifier identifier = new SingaporeNRIC();

        for (String validValue : validValues) {
            assertThat(validValue, identifier.isOfThisType(validValue), is(true));
        }
    }

    @Test
    public void missesInvalidValues() {
        String[] invalidValues = {
                "foo",
                "foo@gmail.com",
        };

        Identifier identifier = new SingaporeNRIC();

        for (String invalidValue : invalidValues) {
            assertThat(invalidValue, identifier.isOfThisType(invalidValue), is(false));
        }
    }
}