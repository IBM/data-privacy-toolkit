/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUKIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class RandomGeneratorsTest {

    @Test
    public void testRandomSSNUK() {

        SSNUKIdentifier identifier = new SSNUKIdentifier();
        
        for(int i = 0; i < 100; i++) {
            String randomSSN = RandomGenerators.generateRandomSSNUK();
            assertTrue(identifier.isOfThisType(randomSSN));
        }
        
    }
    
    @Test
    public void testLuhnGenerator() {
        String body = "402679993722";
        int digit = RandomGenerators.luhnCheckDigit(body);
        assertEquals(3, digit);

        body = "53305186243923";
        digit = RandomGenerators.luhnCheckDigit(body);
        assertEquals(8, digit);
    }

    @Test
    @Disabled
    public void testPerformanceRandomHostname() {
        int N = 1000000;

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            String randomUsername = RandomGenerators.randomHostnameGenerator("ie.ibm.com", 2);
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.println(String.format("%d operations took %d milliseconds (%f msec per op)", N, diff, (double) diff / N));
    }

    @Test
    @Disabled
    public void testPerformanceRandomUsername() {
        int N = 1000000;

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            String randomUsername = RandomGenerators.randomUsernameAndDomain();
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.println(String.format("%d operations took %d milliseconds (%f per op)", N, diff, (double) diff / N));
    }

    @Test
    public void testRandomWithRange() throws Exception {

        int[] bases = {10,-10};

        for(int base: bases) {
            for (int i = 0; i < 100; i++) {
                int random = RandomGenerators.randomWithinRange(base, 10, 10);
                assert (random >= (base - 10) && random <= (base + 10));

                random = RandomGenerators.randomWithinRange(base, 10, 0);
                assert (random <= base && random >= (base - 10));

                random = RandomGenerators.randomWithinRange(base, 0, 10);
                assert (random >= base && random <= (base + 10));
            }
        }
    }

    @Test
    public void testRandomWithRangeDouble() throws Exception {

        double[] bases = {10,-10};
        double lowerBound = 2.5;
        double upperBound = 3.5;

        for(double base: bases) {
            for (int i = 0; i < 100; i++) {
                double random = RandomGenerators.randomWithinRange(base, lowerBound, upperBound);
                assert (random >= (base - lowerBound) && random <= (base + upperBound));

                random = RandomGenerators.randomWithinRange(base, lowerBound, 0);
                assert (random <= base && random >= (base - lowerBound));

                random = RandomGenerators.randomWithinRange(base, 0, upperBound);
                assert (random >= base && random <= (base + upperBound));
            }
        }
    }

    @Test
    public void testGenerateRandomCoordinates() throws Exception {
        Double latitude = 90.0;
        Double longitude = 180.0;

        LatitudeLongitude originalLatitudeLongitude = new LatitudeLongitude(latitude, longitude);

        for(int i = 0; i < 100; i++) {
            LatitudeLongitude randomLatitudeLongitude = RandomGenerators.generateRandomCoordinate(originalLatitudeLongitude, 100);

            assertTrue(randomLatitudeLongitude.getLatitude() >= -90.0);
            assertTrue(randomLatitudeLongitude.getLatitude() <= 90.0);
            assertTrue(randomLatitudeLongitude.getLongitude() >= -180.0);
            assertTrue(randomLatitudeLongitude.getLongitude() <= 180.0);

            assertFalse(originalLatitudeLongitude.equals(randomLatitudeLongitude));

            Double distance = GeoUtils.latitudeLongitudeDistance(originalLatitudeLongitude, randomLatitudeLongitude);
            assertTrue(distance <= (100.0 + 0.5));
        }
    }

    @Test
    public void testRandomDirection() {
        int radius = 100;

        for(int i  = 0; i < 1000; i++) {
            LatitudeLongitude original = RandomGenerators.generateRandomCoordinate();
            LatitudeLongitude randomCoordinate = RandomGenerators.generateRandomCoordinateRandomDirection(original, radius);
            Double distance = GeoUtils.latitudeLongitudeDistance(original, randomCoordinate);
            assertEquals(100.0, distance, 0.1);
        }
    }

    @Test
    public void testGenerateRandomCoordinatesDonut() throws Exception {
        Double latitude = 40.0;
        Double longitude = 120.0;

        LatitudeLongitude originalLatitudeLongitude = new LatitudeLongitude(latitude, longitude);

        for(int i = 0; i < 100; i++) {
            LatitudeLongitude randomLatitudeLongitude =
                    RandomGenerators.generateRandomCoordinate(originalLatitudeLongitude, 50, 100);

            assertTrue(randomLatitudeLongitude.getLatitude() >= -90.0);
            assertTrue(randomLatitudeLongitude.getLatitude() <= 90.0);
            assertTrue(randomLatitudeLongitude.getLongitude() >= -180.0);
            assertTrue(randomLatitudeLongitude.getLongitude() <= 180.0);

            assertFalse(originalLatitudeLongitude.equals(randomLatitudeLongitude));

            Double distance = GeoUtils.latitudeLongitudeDistance(originalLatitudeLongitude, randomLatitudeLongitude);
            assertTrue(distance >= (50.0));
            assertTrue(distance <= (100.0 + 0.5));
        }
    }

    @Test
    public void testRandomHostname() throws Exception {

        String hostname = "1.2.3.4";
        String randomHostname = RandomGenerators.randomHostnameGenerator(hostname, 0);
        assertFalse(randomHostname.equals(hostname));
        assertTrue(new IPAddressIdentifier().isOfThisType(randomHostname));

        hostname = "www.nba.com";
        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            randomHostname = RandomGenerators.randomHostnameGenerator(hostname, 0);
            assertFalse(randomHostname.equals(hostname));

            if(!randomHostname.endsWith(".com")) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);


        hostname = "www.nba.co.uk";

        for(int i = 0; i < 100; i++) {
            randomHostname = RandomGenerators.randomHostnameGenerator(hostname, 1);
            assertFalse(randomHostname.equals(hostname));
            assertTrue(randomHostname.endsWith(".co.uk"));
        }


        //check that hostname without TLD is processed
        hostname = "adasdasdad";
        randomHostname = RandomGenerators.randomHostnameGenerator(hostname, 0);
        assertFalse(randomHostname.equals(hostname));
    }

    @Test
    public void testRandomDate() throws Exception {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String date = RandomGenerators.generateRandomDate(dateFormat);

        LocalDateTime dt = LocalDateTime.parse(date, dateFormat);
        LocalDateTime now = LocalDateTime.now();
        assertTrue(dt.isBefore(now));
    }

    @Test
    public void testRandomHexSequence() {
       assertEquals("", RandomGenerators.randomHexSequence(0));

       int sequenceLength = 5;

       for(int n = 0; n < 100; n++) {
           String randomHex = RandomGenerators.randomHexSequence(sequenceLength);
           assertEquals(sequenceLength * 2, randomHex.length());

           for (int i = 0; i < randomHex.length(); i += 2) {
               String hex = randomHex.substring(i, i + 2);
               int value = Integer.parseInt(hex, 16);
               assertTrue(value >= 0 && value <= 255);
           }
       }

    }
}

