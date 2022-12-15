/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumericIdentifierTest {

    @Test
    public void testNumeric() {
        Identifier identifier = new NumericIdentifier();

        String[] validValues = {"123", "-1234", "+1234", "123.444", "-123.444"};
        for(String value: validValues) {
            assertTrue(identifier.isOfThisType(value));
        }

        String[] invalidValues = {"a123", "-1234a", "+1234a", "123a", "abcd", "", " "};
        for(String value: invalidValues) {
            assertFalse(identifier.isOfThisType(value));
        }

    }
}
