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

public class VINIdentifierTest {

    @Test
    public void testIsOfThisType() {
        VINIdentifier vinIdentifier = new VINIdentifier();

        /* VIN value too short */
        String vin = "ABV3231333";
        assertFalse(vinIdentifier.isOfThisType(vin));

        /* VIN contains invalid characters */
        vin = "ABQ12345678901234"; // char Q is invalid
        assertFalse(vinIdentifier.isOfThisType(vin));
        vin = "AIB12345678901234"; // char I is invalid
        assertFalse(vinIdentifier.isOfThisType(vin));
        vin = "AOB12345678901234"; // char O is invalid
        assertFalse(vinIdentifier.isOfThisType(vin));

        /* VIN contains non-digits or non-letters */
        vin = "1B312-45678901234"; // char O is invalid
        assertFalse(vinIdentifier.isOfThisType(vin));
        vin = "1B312.45678901234"; // char O is invalid
        assertFalse(vinIdentifier.isOfThisType(vin));

        /* VIN contains a not-known WMI 111*/
        vin = "11112345678901234";
        assertFalse(vinIdentifier.isOfThisType(vin));

        vin = "1B312345678901234"; // char O is invalid
        assertTrue(vinIdentifier.isOfThisType(vin));
    }
}
