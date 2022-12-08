/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPAddressIdentifierTest {

    @Test
    public void testIsOfThisType() {
        IPAddressIdentifier identifier = new IPAddressIdentifier();

        assertTrue(identifier.isOfThisType("1.2.3.4"));

        //letters are not allowed
        assertFalse(identifier.isOfThisType("a.b.0.1"));

        //each prefix of IP address should be <= 255
        assertFalse(identifier.isOfThisType("1111.2.3.4"));

        //wrong format
        assertFalse(identifier.isOfThisType(".2.3.4"));

        //valid format
        assertTrue(identifier.isOfThisType("::"));

        String[] validIPv6Addresses = {
                "1:2:3:4:5:6:7:8",
                "1::",
                "1::8",
                "1::7:8",
                "1::6:7:8",
                "1::5:6:7:8",
                "1::4:5:6:7:8",
                "1::3:4:5:6:7:8",
                "fe80::7:8%eth0",
                "::255.255.255.255",
                "::ffff:255.255.255.255",
                "::FFFF:255.255.255.255",
                "::ffff:0:255.255.255.255",
                "::FFFF:0:255.255.255.255",
                "::AABB:0:255.255.255.255",
                "2001:db8:3:4::192.0.2.33",
                "64:ff9b::192.0.2.33",
                "::a8dc:58:194.33.160.31"
        };

        for(String ipv6address: validIPv6Addresses) {
            assertTrue(identifier.isOfThisType(ipv6address), ipv6address);
        }
    }
}
