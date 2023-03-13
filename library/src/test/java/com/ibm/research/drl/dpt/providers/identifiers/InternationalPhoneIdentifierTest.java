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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternationalPhoneIdentifierTest {

    @Test
    public void testIsOfThisType() {
        InternationalPhoneIdentifier identifier = new InternationalPhoneIdentifier();

        String[] validNumbers = {
                "0044 0876653255",
                "00 1 340 1234567",
                "011 44 340 1234567",
                "00 44-1481 1234567",
                "+44-1481 1234567",
                "+44 1481 1234567",
                "00 1-441 1234567",
                "+353-0876653255",
                "+353 0876653255",
                "+353 087 665 3255",
                "+353 087 665  3255",
                "00353-0876653255",
                "+353-(087)6653255",
                "0044-(087)6653255",
                "0044 (087)6653255",
                "+622125669098"
        };

        for (int i = 0; i < validNumbers.length; ++i) {
            String number = validNumbers[i];
            assertTrue(identifier.isOfThisType(number), number);
        }

        String[] invalidNumbers = {
                "+353-0876653255aaaa",
                "+353-0876653255123123123123131231331131212121",
                "+353-(0876653255",
                "622125669098",
                "+622125669098a",
                "+999125669098",
                "+62123",
                "021 44 340 1234567",
                "00 11-1481 1234567",
        };

        for(String invalidNumber: invalidNumbers) {
            assertFalse(identifier.isOfThisType(invalidNumber), invalidNumber);
        }
    }

    @Test
    @Disabled
    public void testPhoneList() throws Exception {
        InternationalPhoneIdentifier identifier = new InternationalPhoneIdentifier();

        try(BufferedReader br = new BufferedReader(new FileReader("/Users/santonat/dev/organizations/international_phone_numbers.csv"))) {
            for (String line; (line = br.readLine()) != null; ) {
                String number = line.trim();
                if(!identifier.isOfThisType(number)) {
                    System.out.println(number);
                }
            }
        }
    }
}
