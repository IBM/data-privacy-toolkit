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
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilsTest {

    @Test
    public void testParse() {
        String v = "-.13";
        double d = Double.parseDouble(v);
        assertThat(Double.toString(d), is("-0.13"));
    }

    @Test
    public void testCreateNumber() {

        String input = "twenty four";
        assertEquals(24L, NumberUtils.createNumber(input).longValue());
        assertEquals(24000L, NumberUtils.createNumber("twenty four thousand").longValue());
        assertEquals(20903L, NumberUtils.createNumber("twenty thousand nine hundred and three").longValue());

    }

    @Test
    public void testCreateNumberOrder() {
        assertEquals(23L, NumberUtils.createNumberOrder("twenty third").longValue());
        assertEquals(93L, NumberUtils.createNumberOrder("ninety third").longValue());
        assertEquals(21L, NumberUtils.createNumberOrder("twenty first").longValue());
        assertEquals(111L, NumberUtils.createNumberOrder("one hundred eleventh").longValue());
        assertEquals(500L, NumberUtils.createNumberOrder("five hundredth").longValue());
    }

    @Test
    public void testCreateWords() {
        assertEquals("zero", NumberUtils.createWords(0));
        assertEquals("one", NumberUtils.createWords(1));
        assertEquals("sixteen", NumberUtils.createWords(16));
        assertEquals("one hundred", NumberUtils.createWords(100));
        assertEquals("one hundred eighteen", NumberUtils.createWords(118));
        assertEquals("two hundred", NumberUtils.createWords(200));
        assertEquals("two hundred nineteen", NumberUtils.createWords(219));
        assertEquals("eight hundred", NumberUtils.createWords(800));
        assertEquals("eight hundred one", NumberUtils.createWords(801));
        assertEquals("one thousand three hundred sixteen", NumberUtils.createWords(1316));
        assertEquals("one million", NumberUtils.createWords(1000000).trim());
        assertEquals("two million", NumberUtils.createWords(2000000).trim());
        assertEquals("three million two hundred", NumberUtils.createWords(3000200));
        assertEquals("seven hundred thousand", NumberUtils.createWords(700000).trim());
        assertEquals("nine million", NumberUtils.createWords(9000000));
        assertEquals("nine million one thousand", NumberUtils.createWords(9001000));
        assertEquals("one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine", NumberUtils.createWords(123456789));
    }

    @Test
    public void testDecimalTrimming() {
        String value = "1.234455";
        assertEquals(value, NumberUtils.trimDecimalDigits(value, -1));

        assertEquals("1", NumberUtils.trimDecimalDigits(value, 0));
        assertEquals("1.23", NumberUtils.trimDecimalDigits(value, 2));
        assertEquals(value, NumberUtils.trimDecimalDigits(value, 200));
    }
}
