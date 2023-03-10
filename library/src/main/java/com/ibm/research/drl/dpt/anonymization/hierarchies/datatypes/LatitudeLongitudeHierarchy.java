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
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier;

import java.io.Serializable;
import java.util.Set;

public class LatitudeLongitudeHierarchy implements GeneralizationHierarchy, Serializable {
    private final String topTerm = "0,0";
    private final static LatitudeLongitudeIdentifier LATITUDE_LONGITUDE_IDENTIFIER = new LatitudeLongitudeIdentifier();
    private final int height = 9;

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public long getTotalLeaves() {
        //As with latitude and longitude, the values are bounded by ±90° and ±180° respectively
        //8 decimal degrees precision
        long decimalPossibilities = 100_000_000L;

        return 180 * decimalPossibilities *
                360 * decimalPossibilities;
    }

    @Override
    public int leavesForNode(String value) {
        return 0;
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        throw new RuntimeException("not supported");
    }

    @Override
    public int getNodeLevel(String value) {
        return 0;
    }

    @Override
    public String getTopTerm() {
        return topTerm;
    }

    private String trimDecimal(String s, int finalDecimals) {
        int idx = s.indexOf('.');
        if (idx < 0) {
            return s;
        }

        int existingDecimals = s.length() - (idx + 1);

        if (finalDecimals < existingDecimals) {
            return s.substring(0, idx + finalDecimals + 1);
        }

        return s;
    }

    private String removeDecimal(String s) {
        int idx = s.indexOf('.');
        if (idx < 0) {
            return s;
        }

        return s.substring(0, idx);
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        if (level == 0) {
            return value;
        }

        if (level >= this.height) {
            return this.topTerm;
        }

        LatitudeLongitude latitudeLongitude = LATITUDE_LONGITUDE_IDENTIFIER.parseCoordinate(value);

        String latitude = latitudeLongitude.getLatitude().toString();
        String longitude = latitudeLongitude.getLongitude().toString();

        if (level == (this.height - 1)) {
            latitude = removeDecimal(latitude);
            longitude = removeDecimal(longitude);
        } else {
            latitude = trimDecimal(latitude, (8 - level));
            longitude = trimDecimal(longitude, (8 - level));
        }

        return new LatitudeLongitude(Double.parseDouble(latitude), Double.parseDouble(longitude), latitudeLongitude.getFormat()).toString();
    }


}
