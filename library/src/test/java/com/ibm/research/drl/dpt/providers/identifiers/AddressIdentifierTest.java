/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.Address;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        AddressIdentifier identifier = new AddressIdentifier();

        /* WIP : (\d+\s){0,1}(?<street>(\w+\s*)+)[St.|Street|Road|Rd.|Avenue|Av.|Drive|Dr.|Boulevard|Blvd.|Court|Ct.]\s*(?<citystate>([A-Za-z]+\s*)*)(?<zip>\d+)\s*(?<country>[A-Za-z]+)*
         */

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
                "Hammersmith Bridge Road, London W6 9EJ, United Kingdom",
                "Hammersmith Bridge Road, London W6 9EJ",
                "20 Rock Road, Blackrock Co. Dublin 15, Ireland",
                "20 Rock Road, Blackrock Co. Dublin 15",
                "191 E MAIN BOULEVARD, QĀ’EM SHAHR 85241, LTU",
                "2505 SACKETT RUN RD",
                "1022 WOODLAND AVE",
                "P.O. BOX 334",
                "PO BOX 297",
                "415 w. Route 66, 201",
                "123 Main Street"
        };

        for (String address: validAddresses) {
            assertTrue(identifier.isOfThisType(address), address);
        }
    }

    @Test
    public void testParseAddress() throws Exception {
        String addressName = "200 E Main St, Phoenix AZ 85123, USA";
        AddressIdentifier identifier = new AddressIdentifier();

        Address address = identifier.parseAddress(addressName);
        assertEquals("200", address.getNumber());
        assertEquals("E MAIN", address.getName());
        assertEquals("ST", address.getRoadType());
        assertEquals("PHOENIX AZ", address.getCityOrState());
        assertEquals("85123", address.getPostalCode());
        assertEquals("USA", address.getCountry());

        addressName = "Hammersmith Bridge Road, London W6 9EJ";
        address = identifier.parseAddress(addressName);
        assertEquals("", address.getNumber());
        assertEquals("", address.getCountry());

        //address without city and country
        addressName = "200 E Main St";
        address = identifier.parseAddress(addressName);
        assertEquals("200", address.getNumber());
        assertEquals("E MAIN", address.getName());
        assertEquals("ST", address.getRoadType());
        assertEquals("", address.getCityOrState());
        assertEquals("", address.getPostalCode());
        assertEquals("", address.getCountry());

        //PO BOX case
        addressName = "PO BOX 777";
        address = identifier.parseAddress(addressName);
        assertTrue(address.isPOBox());
        assertEquals("777", address.getPoBoxNumber());
    }

    @Test
    public void testQA() {

        AddressIdentifier identifier = new AddressIdentifier();

        String[] values = {
                "The patient gives his own history and appears to be a reliable source.",
                "responded and said application still in process"
        };


        for (final String value: values) {
            assertFalse(identifier.isOfThisType(value), value);
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;

        String[] originalValues = new String[]{
                "200 E Main St, Phoenix AZ 85123, USA",
                "PO BOX 1234"
        };

        Identifier identifier = new AddressIdentifier();

        for (String originalValue : originalValues) {
            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                boolean check = identifier.isOfThisType(originalValue);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.println(String.format("%s: %d operations took %d milliseconds (%f per op)",
                    originalValue, N, diff, (double) diff / N));
        }
    }
}
