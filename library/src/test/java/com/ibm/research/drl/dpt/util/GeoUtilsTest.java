/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeoUtilsTest {

    @Test
    public void testXYZToLatLon() {
        double x = 3785510.99716482;
        double y = -425712.308930765;
        double z = 5098442.88234343;

        LatitudeLongitude latitudeLongitude = GeoUtils.xyzToLatlon(x, y, z);

        assertEquals(53.4185907, latitudeLongitude.getLatitude(), 0.0001);
        assertEquals(-6.416436, latitudeLongitude.getLongitude(), 0.0001);
    }

    @Test
    public void testLatLonToXYZ() {

        double latitude = 53.4185907;
        double longitude = -6.416436;

        XYZ xyz = GeoUtils.latlonToXYZ(latitude, longitude);

        assertEquals(3785510.99716482, xyz.getX(), 1.0);
        assertEquals(-425712.308930765, xyz.getY(), 1.0);
        assertEquals(5098442.88234343, xyz.getZ(), 1.0);
    }

    @Test
    public void testLatitudeLongitudeDistance() throws Exception {

        double distance = GeoUtils.latitudeLongitudeDistance(10.0, 10.0, 10.0, 10.0);
        assertEquals(0.0, distance);

        distance = GeoUtils.latitudeLongitudeDistance(53.4185907, -6.4164366,
                53.4162888, -6.4144412);
        assertTrue(distance >= 280.0);
        assertTrue(distance <= 300.0);
    }


}
