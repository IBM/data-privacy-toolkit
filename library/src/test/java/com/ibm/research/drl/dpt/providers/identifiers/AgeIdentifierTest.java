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

import com.ibm.research.drl.dpt.models.Age;
import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class AgeIdentifierTest {

    @Test
    public void testIsOfThisType() {

        String[] validPatterns = new String[] {
                "9 years old",
                "9 YEARS OLD",
                "19 years old",
                "19-years-old",
                "19-year-old",
                "6 months old",
                "6-months old",
                "6-months-old",
                "22-weeks-old",
                "at age 6",
                "at age 6 and 1/2",
                "nine years old",
                "ninety five years old",
                "ninety-five years old",
                "twenty two yrs old",
                "37 yr old",
                "37 yr. old",
                "63 yrs. old",
                "66 yo",
                "66 y/o",
                "DOB: 1945",
                "DOB: 01-12-1945",
                "DOB 01-12-1945",
                "42 yrs. male",
                "42yrs. male",
                "42yrs.  male",
                "42 yrs. female",
                "DOB:  12/11/1925",
                "54 yrs. old",
                "16 y/o female",
                "16y/o female",
                "4 YRS 10/12 MO",
                "Date of Birth: 1981",
                "Date of Birth: 01/05/1981",

                "at the age of 93",
                "on his ninety third birthday",
                "on his fifty three birthday",
                "on her ninety third birthday",
                "on his ninety-third birthday",
                "AGE: 36",
                "26 Yrs man",
                "67yo female",
                "nine-years-old",
                "4 1/2 yo",

                "died age 88",
                "died of leukemia at age of 7",
                "passed away from leukemia at age 7",
                "passed away at age 7",
                "died of Alzheimer's at 90",
                "died 91-old age"
        };

        String[] invalidPatterns = new String[] {
                "9 years",
                "9 years ago"
        };

        AgeIdentifier identifier = new AgeIdentifier();

        for(String pattern: validPatterns) {
            assertTrue(identifier.isOfThisType(pattern), pattern);
        }

        for(String pattern: invalidPatterns) {
            assertFalse(identifier.isOfThisType(pattern), pattern);
        }
    }

    @Test
    public void testOffset() {
        String value = "42 yrs. male";
        AgeIdentifier identifier = new AgeIdentifier();

        Tuple<Boolean, Tuple<Integer, Integer>> result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().longValue());
        assertEquals(7, result.getSecond().getSecond().longValue());
    }

    @Test
    public void testParseAge() {

        AgeIdentifier ageIdentifier = new AgeIdentifier();

        String value = "9 years old";
        Age age = ageIdentifier.parseAge(value);

        assertTrue(age.getYearPortion().exists());
        assertEquals(0, age.getYearPortion().getStart());
        assertEquals(1, age.getYearPortion().getEnd());

        assertFalse(age.getDaysPortion().exists());
        assertFalse(age.getMonthPortion().exists());
        assertFalse(age.getWeeksPortion().exists());

        value = "9 years and 6 months";
        age = ageIdentifier.parseAge(value);

        assertTrue(age.getYearPortion().exists());
        assertEquals(0, age.getYearPortion().getStart());
        assertEquals(1, age.getYearPortion().getEnd());

        assertTrue(age.getMonthPortion().exists());
        assertEquals(12, age.getMonthPortion().getStart());
        assertEquals(13, age.getMonthPortion().getEnd());

        assertFalse(age.getDaysPortion().exists());
        assertFalse(age.getWeeksPortion().exists());

        value = "twenty three years old";
        age = ageIdentifier.parseAge(value);

        assertTrue(age.getYearPortion().exists());
        assertEquals(0, age.getYearPortion().getStart());
        assertEquals(12, age.getYearPortion().getEnd());

        assertFalse(age.getDaysPortion().exists());
        assertFalse(age.getMonthPortion().exists());
        assertFalse(age.getWeeksPortion().exists());

        value = "twenty months old";
        age = ageIdentifier.parseAge(value);

        assertFalse(age.getYearPortion().exists());

        assertTrue(age.getMonthPortion().exists());
        assertEquals(0, age.getMonthPortion().getStart());
        assertEquals(6, age.getMonthPortion().getEnd());

        assertFalse(age.getDaysPortion().exists());
        assertFalse(age.getWeeksPortion().exists());
    }

    @Test
    public void testMissedFromMasking() {
        AgeIdentifier identifier = new AgeIdentifier();

        String target = "5 years, 6 months, and 11 days";

        Age age = identifier.parseAge(target);

        assertNotNull(age);

        assertThat(target.substring(age.getYearPortion().getStart(), age.getYearPortion().getEnd()), is("5"));
        assertThat(target.substring(age.getMonthPortion().getStart(), age.getMonthPortion().getEnd()), is("6"));
        assertThat(target.substring(age.getDaysPortion().getStart(), age.getDaysPortion().getEnd()), is("11"));
    }
}
