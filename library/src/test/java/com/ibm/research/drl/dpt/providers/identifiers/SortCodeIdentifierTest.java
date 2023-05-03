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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SortCodeIdentifierTest {
    @Test
    public void validatesKnownUKISortCodes() throws Exception {
        try (
                InputStream is = SortCodeIdentifierTest.class.getResourceAsStream("/known-uki-sort-codes.csv");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {

            Identifier identifier = new SortCodeIdentifier();

            String line;
            while ( (line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String dashed = parts[0];
                String spaced = parts[1];
                String continuous = parts[2];

                assertThat(dashed, identifier.isOfThisType(dashed), is(true));
                assertThat(spaced, identifier.isOfThisType(spaced), is(true));
                assertThat(continuous, identifier.isOfThisType(continuous), is(true));
            }
        }
    }

    @Test
    public void identifiesValidSortCodes() {
        String[] validSortCodes = new String[] {
                "900201",
                "900287",
                "900324",
                "900519",
                "900578",
                "900623",
                "900703",
                "900746",
                "900770",
                "900877",
                "900922",
                "901028",
                "901095",
                "901140",
                "901204",
                "901239",
                "901298",
        };

        Identifier identifier = new SortCodeIdentifier();

        for (String text : validSortCodes) {
            assertThat(identifier.isOfThisType(text), is(true));
        }
    }

    @Test
    public void missesInvalidSortCodes() {
        String[] invalidSortCodes = new String[] {
                "1234567",
                "12345",
        };

        Identifier identifier = new SortCodeIdentifier();

        for (String text : invalidSortCodes) {
            assertThat(identifier.isOfThisType(text), is(false));
        }
    }

    @Test
    public void missesWronglyFormattedSortCodes() {
        String[] wronglyFormatted = new String[] {
            "50-1110",
            "50 1110",
            "5011-10",
            "50-11 10",
            "50 11-10"
        };

        Identifier identifier = new SortCodeIdentifier();

        for (String text : wronglyFormatted) {
            assertThat(identifier.isOfThisType(text), is(false));
        }
    }
}