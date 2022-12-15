/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.util.GeoUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LatitudeLongitudeIdentifierTest {

    @Test
    public void testIsOfThisType() {

        LatitudeLongitudeIdentifier identifier = new LatitudeLongitudeIdentifier();

        String[] validLatitudeLongitudes = {
                "12:30'23.256547S 12:30'23.256547E",
                "12:12:12.2246N 12:12:12.2246W",
                "N90.00.00 E180.00.00",
                "N90.00.00,E180.00.00",
                "S34.59.33 W179.59.59",
                "N00.00.00 W000.00.00",
                "1.3653974,103.7474993"
        };

        for(String latitudeLongitude: validLatitudeLongitudes) {
            assertTrue(identifier.isOfThisType(latitudeLongitude));
        }
    }

    @Test
    public void testIsNotOfThisType() {
        LatitudeLongitudeIdentifier identifier = new LatitudeLongitudeIdentifier();

        String[] invalidLatitudeLongitudes = {
                "12:12:12.223546\"N",
                "12:12:12.2246N",
                "15:17:6\"S",
                "12°30'23.256547\"S",
                "12°30'23.256547S",
                "N91.00.00 E181.00.00",
                "Z34.59.33 W179.59.59",
                "N00.00.00 W181.00.00",
                "12.2225",
                "15.25.257S",
                "51° 31.7' N",
                "AA:BB:CC.DDS",
                "153.418596,-6.4163855",
                "53.418596,-186.4163855"
        };

        for(String latitudeLongitude: invalidLatitudeLongitudes) {
            assertFalse(identifier.isOfThisType(latitudeLongitude));
        }
    }

    @Test
    public void testParseCompassFormat() {
        LatitudeLongitudeIdentifier identifier = new LatitudeLongitudeIdentifier();
        String coords = "12:30'23.256547S 12:30'23.256547E";

        LatitudeLongitude latitudeLongitude = identifier.parseCompassFormat(coords);
        assertNotNull(latitudeLongitude);
        assertEquals(latitudeLongitude.getLatitude(), -GeoUtils.degreesToDecimal(12.0, 30.0, 23.256547));
        assertEquals(latitudeLongitude.getLongitude(), GeoUtils.degreesToDecimal(12.0, 30.0, 23.256547));

        coords = "S34.59.33 W179.59.59";
        latitudeLongitude = identifier.parseCompassFormat(coords);
        assertNotNull(latitudeLongitude);
        assertEquals(latitudeLongitude.getLatitude(), -GeoUtils.degreesToDecimal(34.0, 59.0, 33.0));
        assertEquals(latitudeLongitude.getLongitude(), -GeoUtils.degreesToDecimal(179.0, 59.0, 59.0));

        coords = "S90.00.00 W179.59.59";
        latitudeLongitude = identifier.parseCompassFormat(coords);
        assertNotNull(latitudeLongitude);
        assertEquals(latitudeLongitude.getLatitude(), -GeoUtils.degreesToDecimal(90.0, 0.0, 0.0));
        assertEquals(latitudeLongitude.getLongitude(), -GeoUtils.degreesToDecimal(179.0, 59.0, 59.0));
    }
    
    @Test
    @Disabled
    public void testQuickCheckBenefit() {
        LatitudeLongitudeIdentifier identifier = new LatitudeLongitudeIdentifier();
        int runs = 1000000;
        
        String value = "this is definitely not a match";
        
        long start = System.currentTimeMillis();
        int matches = 0;
        for(int i = 0; i < runs; i++) {
            if (identifier.isOfThisType(value)) {
                matches++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("time : " + (end - start));
        System.out.println("matches: " + matches);

        start = System.currentTimeMillis();
        matches = 0;
        for(int i = 0; i < runs; i++) {
            if (quickCheck(value)) {
                if (identifier.isOfThisType(value)) {
                    matches++;
                }
            }
        }
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start));
        System.out.println("matches: " + matches);
    }

    private boolean quickCheck(String value) {
        for(int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                return true;
            }
        }
        
        return false;
    }
}
