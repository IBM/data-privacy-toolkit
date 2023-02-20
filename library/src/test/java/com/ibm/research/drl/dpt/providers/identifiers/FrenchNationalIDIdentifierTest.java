package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrenchNationalIDIdentifierTest {
    @Test
    public void invalid() {
        String[] invalidValues = new String[] {
                "FOO BAR",
                "123456+78978945",

        };

        FrenchNationalIDIdentifier identifier = new FrenchNationalIDIdentifier();

        for (String value : invalidValues) {
            assertFalse(identifier.isOfThisType(value), value);
        }
    }

    @Test
    public void valid() {
        String[] validValues = new String[] {
                "1 51 02 46102 043 25",
                "379058496820192",
                "2 79 05 84 968 201 91",
                "279053A96820192",
        };

        FrenchNationalIDIdentifier identifier = new FrenchNationalIDIdentifier();

        for (String value : validValues) {
            assertTrue(identifier.isOfThisType(value), value);
        }
    }
}