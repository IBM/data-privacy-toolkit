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

public class UKPostCodeIdentifierTest {
    @Test
    public void support7characterPostCodes () {
        Identifier identifier = new UKPostCodeIdentifier();

        assertThat(identifier.isOfThisType("SO171BJ"), is(true));
    }

    @Test
    public void support8characterPostCodes () {
        Identifier identifier = new UKPostCodeIdentifier();

        assertThat(identifier.isOfThisType("SO17 1BJ"), is(true));
    }

    @Test
    public void possiblyAddressableButIncorrect() {
        String[] variants = new String[] {
                "S0171BJ",
                "SOI7 1BJ",
                "SO17 IBJ",
                "S0I7 IBJ",
                "SO I7 Ibj",
                "S017 1BJ",
                "So17IBJ",
                "SO1 71BJ"
        };

        Identifier identifier = new UKPostCodeIdentifier();
        for (String variant : variants) {
            assertThat(variant, identifier.isOfThisType(variant), is(false));
        }
    }

    @Test
    public void supporsForKnownFormats() {
        /*
        AN_NAA 	 B1 1AA 	 Royal Mail Central Birmingham Delivery Office
        ANN_NAA 	 M60 2LA 	 Manchester City Council
        AAN_NAA 	 SA6 7JL 	 Driver and Vehicle Licensing Authority, Swansea
        AANN_NAA 	 SO17 1BJ 	 University of Southampton
        ANA_NAA 	 W1D 1AN 	 Tottenham Court Road Tube Station, London
        AANA_NAA 	 EC2R 8AH 	 Bank of England, London
        */

        String[] formats = new String[] {
                "B1 1AA",
                "M60 2LA",
                "SA6 7JL",
                "SO17 1BJ",
                "W1D 1AN",
                "EC2R 8AH",

        };

        Identifier identifier = new UKPostCodeIdentifier();
        for (String format : formats) {
            assertThat(format, identifier.isOfThisType(format), is(true));
        }
    }
}