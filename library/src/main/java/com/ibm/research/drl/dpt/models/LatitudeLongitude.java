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
package com.ibm.research.drl.dpt.models;

import java.util.Objects;

public class LatitudeLongitude {
    /**
     * The Latitude.
     */
    double latitude;
    /**
     * The Longitude.
     */
    double longitude;
    /**
     * The Format.
     */
    LatitudeLongitudeFormat format;

    /**
     * Instantiates a new Latitude longitude.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public LatitudeLongitude(double latitude, double longitude) {
        this(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Instantiates a new Latitude longitude.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param format    the format
     */
    public LatitudeLongitude(double latitude, double longitude, LatitudeLongitudeFormat format) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.format = format;
    }

    /**
     * Gets format.
     *
     * @return the format
     */
    public LatitudeLongitudeFormat getFormat() {
        return this.format;
    }

    /**
     * Sets format.
     *
     * @param format the format
     */
    public void setFormat(LatitudeLongitudeFormat format) {
        this.format = format;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return this.latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        LatitudeLongitude latLon = (LatitudeLongitude) obj;
        return Objects.equals(latLon.getLatitude(), latitude) && Objects.equals(latLon.getLongitude(), longitude);
    }

    @Override
    public String toString() {
        if (format == LatitudeLongitudeFormat.DECIMAL) {
            return String.format("%.8f,%.8f", getLatitude(), getLongitude());
        }

        String ns = "N";
        String ew = "E";

        double latitude = this.latitude;
        if (latitude < 0) {
            ns = "S";
            latitude = -latitude;
        }

        double longitude = this.longitude;
        if (longitude < 0) {
            ew = "W";
            longitude = -longitude;
        }

        int nsDegrees = (int) latitude;
        int nsMinutes = (int) ((latitude - nsDegrees) * 60);
        double nsSeconds = (latitude - nsDegrees - (double) nsMinutes / 60.0) * 3600;
        int ewDegrees = (int) longitude;
        int ewMinutes = (int) ((longitude - ewDegrees) * 60);
        double ewSeconds = (longitude - ewDegrees - (double) ewMinutes / 60.0) * 3600;

        if (format == LatitudeLongitudeFormat.COMPASS) {
            return String.format("%s%02d.%02d.%02d %s%02d.%02d.%02d",
                    ns, nsDegrees, nsMinutes, (int) nsSeconds, ew, ewDegrees, ewMinutes, (int) ewSeconds);
        }

        return String.format("%02d:%02d'%f%s %02d:%02d'%f%s",
                nsDegrees, nsMinutes, nsSeconds, ns, ewDegrees, ewMinutes, ewSeconds, ew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, format);
    }
}
