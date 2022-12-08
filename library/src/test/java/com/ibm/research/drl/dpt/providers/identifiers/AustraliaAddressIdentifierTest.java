/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AustraliaAddressIdentifierTest {
    @Test
    public void testRealWorldData() {
        String[] data = {
                "100 Flushcombe Road\n" +
                        "BLACKTOWN NSW 2148",

                "99 George Street\n" +
                        "PARRAMATTA NSW 2150",

                "PO Box 99\n" +
                        "PARRAMATTA NSW 2124"
        };

        Identifier identifier = new AustraliaAddressIdentifier();

        for (String value : data) {
            assertTrue(identifier.isOfThisType(value), value);
        }
    }

    @Test
    public void testWithRealWorldData() {
        String[] validAddresses = {
                "534 Erewhon St",
                "534 Erewhon St PeasantVille",
                "534 Erewhon St PeasantVille, Rainbow",
                "534 Erewhon St PeasantVille, Rainbow, Vic",
                "534 Erewhon St PeasantVille, Rainbow, Vic  3999",
        };

        Identifier identifier = new AustraliaAddressIdentifier();

        for (String validAddress : validAddresses) {
            assertTrue(identifier.isOfThisType(validAddress), validAddress);
        }
    }
}