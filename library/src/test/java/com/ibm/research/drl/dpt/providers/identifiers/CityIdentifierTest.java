/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        CityIdentifier identifier = new CityIdentifier();

        String[] validCities = {
                "Athens",
                "athens",
                "Dublin",
                "New York City"
        };

        for(String city: validCities) {
            assertTrue(identifier.isOfThisType(city));
        }

        String[] invalidCities = {
                "12344",
                "Doblin"
        };

        for(String city: invalidCities) {
            assertFalse(identifier.isOfThisType(city));
        }
    }
}
