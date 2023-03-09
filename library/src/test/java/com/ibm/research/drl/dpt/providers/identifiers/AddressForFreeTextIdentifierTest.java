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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddressForFreeTextIdentifierTest {
    @Test
    public void multilines() {
        String[] validPOBoxes = {
                "88 BROOKLYN ST\n" +
                        "Brookl Hill, NY 12345",
                "7 Blackmoore Cir\n" +
                        "Dublin, NY 12345",
                "7 Blackmoore Cir\n" +
                        "    Dublin, NY 123450000",
                "40 SOMETHING RD\n" +
                        "City, MA 123121111",
                "Apt 1\n" +
                        "Dublin, NY 12345",
                "123 BLACKMORE AVE APT 1\n" +
                        "BRONX, NY 12345",

                "123 East Side Mall\n" +
                        "Bronx, NY 12345-6789",

                "123 Bronx AV.\n" +
                        "Bronx, NY 12345",

                        "APT C\n" +
                        "5800 SPRINGFIELD GARDENS CIR\n" +
                        "SPRINGFIELD VA 22162-1058",

                "1500 E MAIN AVE STE 201\n" +
                        "SPRINGFIELD VA 22162-1010",

                "123 Cobbleston St\n" +
                        "Westwood, MA 02090",

                "345 Westwood Street\n" +
                        "Watertown, MA 02472-2811",

                "123 TALBOT AVE\n" +
                        "Boston, MA 12345",
                
                "Apt#123\n" +
                        "Auburndale, MA 12345",
                
                "Apt 123\n" +
                        "Auburndale, MA 12345",
        };

        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();

        for (final String POBOX : validPOBoxes) {
            assertTrue(identifier.isOfThisType(POBOX), POBOX);
        }
    }

    @Test
    public void lastLine() {
        String[] validLastLines = {
                "Westwood, MA 02090",
        };

        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();

        for (final String line : validLastLines) {
            assertTrue(identifier.isOfThisType(line), line);
        }
    }

    @Test
    public void deliveryAddressLine() {
        String[] validDeliveryAddressLines = {
                "101 MAIN ST",
                "101 MAIN ST APT 12",
                "101 W MAIN ST APT 12",
                "101 W MAIN ST S APT 12",
                "123 Cobbleston St",
                "123 Brookline Ave., Boston, MA 12345"
        };

        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();

        for (final String line : validDeliveryAddressLines) {
            assertTrue(identifier.isOfThisType(line), line);
        }
    }
    
    @Test
    public void testInvalid() {
        String[] invalidAddressLines = {
                "168 that represents the corresponding ontology file as",
                "500 mg",
                "20 minutes",
                "80 mg after each meal and I suggest that he",
                "12 hours before your test",
                "1 liter",
                "5  refills",
                "121  40-197",
                "108 High",
                "5 Blood",
                "40 generic brand",
                "John Does\n" +
                        "123 Brown Street\n" +
                        "Braintree, MA 03284-3222",
                "John Doe",
                "Kellaris prime",
                "50mg of ibuprophenis",
                "11 and is scheduled for her first actual",
                "130-167 which is significant improvement since increase  in",
                "2 days left upper jaw line near ear is",
                "1 right hand is",
                "12 months and stop CT",
                "1 week with pcp Dr.",
                "16 per Dr.",
        };

        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();

        for (final String line : invalidAddressLines) {
            assertFalse(identifier.isOfThisType(line), line);
        }
    }

    @Test
    public void recognizesValidAddresses() {
        String[] validAddresses = {
                "200 E Main St, Phoenix AZ 85123, USA",
                "200 E Main St., Phoenix AZ 85123, USA",
                "200 Main Street, Phoenix AZ 85123, USA",
                "200 Main Boulevard, Phoenix AZ 85123, USA",
                "200 Main Blvd, Phoenix AZ 85123, USA",
                "200 Main Blvd., Phoenix AZ 85123, USA",
                "200 Main Drive, Phoenix AZ 85123, USA",
                "200 Main Dr., Phoenix AZ 85123, USA",
                "200 Main Court, Phoenix AZ 85123, USA",
                "200 Main Ct., Phoenix AZ 85123, USA",
                "300 Bolyston Ave, Seattle WA 98102",
                "300 Bolyston Avenue, Seattle WA 98102",
                "300 Bolyston Ave., Seattle WA 98102",
                "2505 SACKETT RUN RD",
                "1022 WOODLAND AVE",

                "123 MAGNOLIA ST",
                "1500 E MAIN AVE STE 201",
                "102 MAIN ST APT 101",
                "1356 EXECUTIVE DR STE 202",
                "1600 CENTRAL PL BLDG 14",
                "55 SYLVAN BLVD RM 108",

                "425 FLOWER BLVD",
                "425 FLOWER BLVD # 72",
                "5800 SPRINGFIELD GARDENS CIR",
                "1201 BROAD ST E",
                "1401 S. MAIN ST.",
                "101 MAIN ST",
                "101 MAIN ST APT 12",
                "101 W MAIN ST APT 12",
                "101 W MAIN ST S APT 12",

                "12 E BUSINESS LN STE 209",
               // "415 w. Route 66, 201",
        };

        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();

        for (final String address : validAddresses) {
            if (address.isEmpty()) continue;
            assertTrue(identifier.isOfThisType(address), address);
        }
    }
    
    @Test
    @Disabled
    public void testPerformance() {
        AddressForFreeTextIdentifier identifier = new AddressForFreeTextIdentifier();
        
        long tsStart = System.currentTimeMillis();
        long total = 0;
        
        String input = "abc def";
        
        for(int i = 0; i < 1000000; i++) {
            total += identifier.isOfThisType(input) ? 1 : 0;
        }
        
        long tsEnd = System.currentTimeMillis();

        System.out.println("total: " + total);
        System.out.println("time: " + (tsEnd - tsStart));
    }
}