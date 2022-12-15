/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.util.CountryNameSpecification;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountryManagerTest {

    @Test
    public void testLookupSuccessful() throws Exception {
        CountryManager countryManager = CountryManager.getInstance();
        String country = "United States of America";
        assertTrue(countryManager.isValidKey(country));
        assertTrue(countryManager.isValidCountry(country, CountryNameSpecification.NAME));

        //check that 3 ISO letter code is matched
        country = "USA";
        assertTrue(countryManager.isValidKey(country));
        assertTrue(countryManager.isValidCountry(country, CountryNameSpecification.ISO3));

        country = "GB";
        assertTrue(countryManager.isValidKey(country));
        assertTrue(countryManager.isValidCountry(country, CountryNameSpecification.ISO2));

        //check that the lowercase version is also matched
        country = "brazil";
        assertTrue(countryManager.isValidKey(country));

        //check that friendly name is also matched
        country = "Vietnam";
        assertTrue(countryManager.isValidKey(country));

        country = "sierra leone";
        assertTrue(countryManager.isValidKey(country));

        country = "Foobar";
        assertFalse(countryManager.isValidKey(country));
    }

    @Test
    public void testRandomCountryGenerator() throws Exception {
        CountryManager countryManager = CountryManager.getInstance();
        //test random country
        assertTrue(countryManager.isValidKey(countryManager.getRandomKey(CountryNameSpecification.NAME)));

        String exceptionCountry = "US";
        for(int i = 0; i < 1000; i++) {
            String randomCountry = countryManager.getRandomKey();
            assertFalse(randomCountry.equals(exceptionCountry));
        }
    }

    @Test
    public void testClosestCountry() throws Exception {
        CountryManager countryManager = CountryManager.getInstance();
        String originalCountry = "Greece";
        String[] neighbors = {
                "CYPRUS",
                "MALTA",
                "TURKEY",
                "SERBIA",
                "BOSNIA AND HERZEGOVINA",
                "ROMANIA",
                "MONTENEGRO",
                "BULGARIA",
                "ALBANIA",
                "GREECE",
                "MACEDONIA (THE FORMER YUGOSLAV REPUBLIC OF)"
        };

        List<String> neighborsList = Arrays.asList(neighbors);
        for(int i =0; i < 100; i++) {
            String randomCountry = countryManager.getClosestCountry(originalCountry, 10);
            assertTrue(neighborsList.contains(randomCountry.toUpperCase()));
        }
    }

    @Test
    public void testClosestCountryOverrun() throws Exception {
        CountryManager countryManager = CountryManager.getInstance();
        String originalCountry = "Greece";

        for(int i =0; i < 100; i++) {
            String randomCountry = countryManager.getClosestCountry(originalCountry, 100000);
            assertTrue(countryManager.isValidKey(randomCountry));
        }
    }
}
