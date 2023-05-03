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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.*;

public class NameIdentifierTest {

    @Test
    @Disabled
    public void fixWeirdDetection() {
        String[] incorrect = {
          "Figures"
        };

        NameIdentifier identifier = new NameIdentifier();

        for (String term : incorrect) {
            assertFalse(identifier.isOfThisType(term), term);
        }
    }

    @Test
    public void testIsOfThisType() throws Exception {
        NameIdentifier identifier = new NameIdentifier();

        assertFalse(identifier.isOfThisType("12308u499234802"));
        assertFalse(identifier.isOfThisType("84032-43092-3242"));

        assertFalse(identifier.isOfThisType("32 Carter Avn."));
        assertFalse(identifier.isOfThisType("15 Kennedy Avenue"));
        assertFalse(identifier.isOfThisType("Thompson Avn 1000"));

        assertTrue(identifier.isOfThisType("Carrol, John J."));
        assertTrue(identifier.isOfThisType("Carrol, John"));
        assertTrue(identifier.isOfThisType("Patrick K. Fitzgerald"));
        assertTrue(identifier.isOfThisType("Patrick Fitzgerald"));
        assertTrue(identifier.isOfThisType("Kennedy John"));
    }

    @Test
    public void testIsOfThisTypeWithOffset() throws Exception {
        NameIdentifier identifier = new NameIdentifier();

        String value = "Carrol, John";
        Tuple<Boolean, Tuple<Integer, Integer>> result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length(), result.getSecond().getSecond().intValue());

        value = "Bhan,";
        result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length() - 1, result.getSecond().getSecond().intValue());

        value = "Carrol, John,";
        result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length() - 1, result.getSecond().getSecond().intValue());

    }

    public List<String> fileContentsAsList(InputStream inputStream) throws Exception{
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));

        String line = reader.readLine();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();

        return lines;
    }

    @Test
    public void testSurnamesTop1000US() throws Exception {
        try (InputStream inputStream = NameIdentifierTest.class.getResourceAsStream("/top1KsurnamesUS.csv")) {
            NameIdentifier identifier = new NameIdentifier();
            List<String> surnames = fileContentsAsList(inputStream);

            double totalSurnames = surnames.size();
            double totalMatches = 0;

            for (String surname : surnames) {
                if (identifier.isOfThisType(surname)) {
                    totalMatches += 1.0;
                }
            }

            assertThat(totalMatches/totalSurnames, closeTo(0.75, 0.02));
        }
    }

    @Test
    public void testNamesTop1000US() throws Exception {
        NameIdentifier identifier = new NameIdentifier();
        String[] filenames = {"/top1200maleNamesUS.csv", "/top1000femaleNamesUS.csv"};

        for (String filename: filenames) {
            List<String> names = fileContentsAsList(this.getClass().getResourceAsStream(filename));

            int totalNames = 0;
            int totalMatches = 0;

            for (String name : names) {
                totalNames += 1;
                if (identifier.isOfThisType(name)) {
                    totalMatches += 1;
                }
            }

            assertTrue(((double) totalMatches / (double) totalNames) > 0.70);
        }
    }
}
