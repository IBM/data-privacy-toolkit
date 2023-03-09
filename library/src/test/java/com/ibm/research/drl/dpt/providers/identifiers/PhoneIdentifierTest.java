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

import com.ibm.research.drl.dpt.models.PhoneNumber;
import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;


public class PhoneIdentifierTest {

    @Test
    public void testIfTheIncorrectDetectionsAreFixed() {
        String[] invalidPhones = {
                "105-150",
                "400-500",
                "2001 1126302"
        };

        PhoneIdentifier identifier = new PhoneIdentifier();

        for (String number : invalidPhones) {
            assertFalse(identifier.isOfThisType(number), number);
        }
    }

    @Test
    public void extractDataCorrectly() {
        PhoneIdentifier identifier = new PhoneIdentifier();

        String[] validNumbers = {
                "+353-0876653255",
                "00353-0876653255",
                "+353-(087)6653255",
                "0044-(087)6653255",
                "0044 (087)6653255",
                "0044 0876653255",
                "Pgr: 123-45678",
                "Ph: 123-45678",
                "Ph: (781) 555-1234",
                "(781) 555-1234",
                "(781)555-1234",
                "Phone: (781) 555-1234",
                "phone: (781) 555-1234",
                "Fax: (781) 555-1234",
                "Phone (781) 555-1234",
                "Fax (781) 555-1234",
                "Phone #(781) 555-1234",
                "phone #(781) 555-1234",
                "phone #617-333-5555",
                "617-333-5555",
                "3471234567",//New York
                "2601234555",//Indiana
                "Contact: (781) 555-1234",
        };

        String[] validationData = {
                "+353-0876653255",
                "00353-0876653255",
                "+353-(087)6653255",
                "0044-(087)6653255",
                "0044 (087)6653255",
                "0044 0876653255",
                "123-45678",
                "123-45678",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781)555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "(781) 555-1234",
                "617-333-5555",
                "617-333-5555",
                "3471234567",//New York
                "2601234555",//Indiana
                "(781) 555-1234",
        };


        for (int i = 0; i < validNumbers.length; ++i) {
            String number = validNumbers[i];
            String validationValue = validationData[i];

            Tuple<Boolean, Tuple<Integer, Integer>> response = identifier.isOfThisTypeWithOffset(number);

            assertTrue(response.getFirst(), number);

            Tuple<Integer, Integer> range = response.getSecond();

            assertNotNull(range, number);

            assertThat(range.getFirst() + range.getSecond(), lessThanOrEqualTo(number.length()));

            String extractedString = number.substring(range.getFirst(), range.getFirst() + range.getSecond());

            assertEquals(validationValue, extractedString, number);
        }
    }

    @Test
    public void itDoesNotDectectWronglyFormattedNumbers() {
        PhoneIdentifier identifier = new PhoneIdentifier();

        String[] invalidNumbers = {
//                "4251844", // commented because valid according to the US
                "873320071105095646",
                "140 39-150",
                "+44",
                "+44-123444a122",
                "33917",
                "260123455a" //it contains letters
        };

        for (String number: invalidNumbers) {
            assertFalse(identifier.isOfThisType(number), number);
        }

    }

    @Test
    public void testIsOfThisType() {
        PhoneIdentifier identifier = new PhoneIdentifier();

        String[] validNumbers = {
                "+353-0876653255",
                "00353-0876653255",
                "+353-(087)6653255",
                "0044-(087)6653255",
                "0044 (087)6653255",
                "0044 0876653255",
                "Pgr: 123-45678",
                "Ph: 123-45678",
                "Ph: (781) 555-1234",
                "(781) 555-1234",
                "(781)555-1234",
                "Phone: (781) 555-1234",
                "phone: (781) 555-1234",
                "Fax: (781) 555-1234",
                "Phone (781) 555-1234",
                "Fax (781) 555-1234",
                "Phone #(781) 555-1234",
                "phone #(781) 555-1234",
                "phone #617-333-5555",
                "617-333-5555",
                "3471234567",//New York
                "2601234555",//Indiana
        };

        for (String number: validNumbers) {
            assertTrue(identifier.isOfThisType(number), number);
        }
    }

    @Test
    public void wronglyIdentifiedPhone() {
        String[] incorrectPhones = {
                "1993.04.30",
                "12345678901234567890"
        };

        PhoneIdentifier identifier = new PhoneIdentifier();

        for (String incorrectPhone : incorrectPhones) {
            assertFalse(identifier.isOfThisType(incorrectPhone), incorrectPhone);
        }
    }

    @Test
    public void supportUKNumbers() {
        List<String> validPhones = Arrays.asList(
                "020 76001818",
                "02076001818",
                "(+44)02076001818",
                "+442076001818"
        );

        PhoneIdentifier identifier = new PhoneIdentifier();

        for (String validPhone : validPhones) {
            assertTrue(identifier.isOfThisType(validPhone), validPhone);
        }
    }

    @Test
    public void supportUSNumbers() {
        List<String> validPhones = Arrays.asList(
                "754-3010", // Local
                "(541) 754-3010", //Domestic
                "541-754-3010", // International
                "+1-541-754-3010", // International
                "1-541-754-3010", // Dialed in the US
                "001-541-754-3010" // Dialed from Germany
                //"191 541 754 3010" // Dialed from France
        );

        PhoneIdentifier identifier = new PhoneIdentifier();

        for (String validPhone : validPhones) {
            assertTrue(identifier.isOfThisType(validPhone), validPhone);
        }
    }

    @Test
    public void supportWeirdFormats() {
        List<String> supportedFormats = Arrays.asList(
                "+99(099)9999-9999",
                "0099(099)9999-9999"
        );

        PhoneIdentifier identifier = new PhoneIdentifier();

        for (String validPhone : supportedFormats) {
            assertTrue(identifier.isOfThisType(validPhone), validPhone);
        }
    }

    @Test
    @Disabled
    public void testSlowPerformance() throws Exception {
        
        String input = "40404040404040404040404040404040404040404040404040404040404040E6D6D9D240C3D6D4D740404040404040404040404040404040404040404040404040404040404040404040404040404040";
        
        PhoneIdentifier identifier = new PhoneIdentifier();
        
        long start = System.currentTimeMillis();
        boolean matches = identifier.isOfThisType(input);
        
        System.out.println("matches: " + matches);
        System.out.println("time: " + (System.currentTimeMillis() - start));
    }
    
    @Test
    public void testGetPhone() {
        PhoneIdentifier identifier = new PhoneIdentifier();
        String phoneNumber = "+353-0876653255";

        PhoneNumber number = identifier.getPhoneNumber(phoneNumber);
        assertThat(phoneNumber, number.getPrefix(), is("+"));
        assertThat(phoneNumber, number.getCountryCode(), is("353"));
        assertThat(phoneNumber, number.getAreaCode(), is("087"));
        assertThat(phoneNumber, number.getNumber(), is("6653255"));

        //US number with prefix
        phoneNumber = "+1-3471234567";

        number = identifier.getPhoneNumber(phoneNumber);

        assertThat(phoneNumber, number.getPrefix(), is("+"));
        assertThat(phoneNumber, number.getCountryCode(), is("1"));
        assertThat(phoneNumber, number.getAreaCode(), is("347"));
        assertThat(phoneNumber, number.getNumber(), is("1234567"));

        //US number
        phoneNumber = "3471234567";

        number = identifier.getPhoneNumber(phoneNumber);
        assertThat(phoneNumber, number.getPrefix(), is("+"));
        assertThat(phoneNumber, number.getCountryCode(), is("1"));
        assertThat(phoneNumber, number.getAreaCode(), is("347"));
        assertThat(phoneNumber, number.getNumber(), is("1234567"));
    }
}
