package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class GermanTaxIdentificationNumberIdentifierTest {
    @Test
    public void testPositive() {
        Identifier identifier = new GermanTaxIdentificationNumberIdentifier();

        String[] correctValues = new String[] {
                "12 345 678 901",
                "12345678901",
        };

        for (String value : correctValues) {
            assertThat(value, identifier.isOfThisType(value), is(true));
        }
    }

    @Test
    public void testNegative() {
        Identifier identifier = new GermanTaxIdentificationNumberIdentifier();

        String[] invalidValues = new String[] {
                "96480255173",
                "06480255173",
                "96480255171",
                "96480255573",
                "96580245173",
        };

        for (String value : invalidValues) {
            assertThat(value, identifier.isOfThisType(value), is(false));
        }
    }
}