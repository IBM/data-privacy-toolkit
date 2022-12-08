/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;


public class USPhoneIdentifierTest {
    @Test
    public void correctlyFormatted() {
        String[] validPhones = {
                "Pgr: 123-4567",
                "Ph: 123-4567",
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
                "(415) 555-2671",
                "(415)555-2671",
                "555-2671",
                "415-555-2671",
                "phone #415-555-2671",
                "#415-555-2671",
                "877 218 1190"
        };

        USPhoneIdentifier identifier = new USPhoneIdentifier();

        for (String validPhone : validPhones) {
            assertTrue(identifier.isOfThisType(validPhone), validPhone);
        }
    }

    @Test
    public void phoneNumbersAreCorrectlyExtracted() {
            String[] validPhones = {
                    "Pgr: 123-4567",
                    "Ph: 123-4567",
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
                    "(415) 555-2671",
                    "(415)555-2671",
                    "555-2671",
                    "415-555-2671",
                    "1-123-123-1234"
            };

            String[] phones = {
                    "123-4567",
                    "123-4567",
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
                    "(415) 555-2671",
                    "(415)555-2671",
                    "555-2671",
                    "415-555-2671",
                    "1-123-123-1234"
            };

            USPhoneIdentifier identifier = new USPhoneIdentifier();

        for (int i = 0; i < validPhones.length; ++i) {
            String number = validPhones[i];
            String validationValue = phones[i];

            Tuple<Boolean, Tuple<Integer, Integer>> response = identifier.isOfThisTypeWithOffset(number);

            assertTrue(response.getFirst(), number);

            Tuple<Integer, Integer> range = response.getSecond();

            assertNotNull(range, number);

            assertThat(number,range.getFirst() + range.getSecond(), lessThanOrEqualTo(number.length()));

            String extractedString = number.substring(range.getFirst(), range.getFirst() + range.getSecond());

            assertEquals(validationValue, extractedString, number);
        }
    }

    @Test
    public void testIfTheIncorrectDetectionsAreFixed() {
        String[] invalidPhones = {
                "105-150",
                "400-500",
                "2001 1126302"
        };

        USPhoneIdentifier identifier = new USPhoneIdentifier();

        for (String number : invalidPhones) {
            assertFalse(identifier.isOfThisType(number), number);
        }
    }

    @Test
    public void itDoesNotDectectWronglyFormattedNumbers() {
        USPhoneIdentifier identifier = new USPhoneIdentifier();

        String[] invalidNumbers = {
                "4251844", // commented because valid according to the US
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
    public void wronglyIdentifiedPhone() {
        String[] incorrectPhones = {
                "1993.04.30",
                "12345678901234567890"
        };

        USPhoneIdentifier identifier = new USPhoneIdentifier();

        for (String incorrectPhone : incorrectPhones) {
            assertFalse(identifier.isOfThisType(incorrectPhone), incorrectPhone);
        }
    }
}
