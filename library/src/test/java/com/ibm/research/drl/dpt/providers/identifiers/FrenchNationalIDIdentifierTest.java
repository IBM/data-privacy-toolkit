package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrenchNationalIDIdentifierTest {
    @Test
    public void invalidExamples() {
        String[] invalidValues = new String[] {
                "FOO BAR",
                "123456+78978945",
                "1 51 02 46102 043 25",
                "379058496820192",

        };

        FrenchNationalIDIdentifier identifier = new FrenchNationalIDIdentifier();

        for (String value : invalidValues) {
            assertFalse(identifier.isOfThisType(value), value);
        }

    }

    @Test
    public void validExamples() {
        String[] validValues = new String[] {
                "2 79 05 84968 201 92",
                "279058496820192",
//                "279053A96820192",  // unclear from the specs how to handle letters for the parity check
                "151024610204372",
                "1 51 02 46102 043 72",
        };

        FrenchNationalIDIdentifier identifier = new FrenchNationalIDIdentifier();

        for (String value : validValues) {
            assertTrue(identifier.isOfThisType(value), value);
        }
    }
}