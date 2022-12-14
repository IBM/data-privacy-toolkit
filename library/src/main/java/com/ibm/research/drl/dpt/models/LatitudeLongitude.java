/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.util.Objects;

public class LatitudeLongitude {
    /**
     * The Latitude.
     */
    Double latitude;
    /**
     * The Longitude.
     */
    Double longitude;
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
    public LatitudeLongitude(Double latitude, Double longitude) {
        this(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Instantiates a new Latitude longitude.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param format    the format
     */
    public LatitudeLongitude(Double latitude, Double longitude, LatitudeLongitudeFormat format) {
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
            /*
            StringBuilder builder = new StringBuilder();
            builder.append(getLatitude());
            builder.append(",");
            builder.append(getLongitude());
            return builder.toString();
            */
        }

        String ns = "N";
        String ew = "E";

        Double latitude = this.latitude;
        if (latitude < 0) {
            ns = "S";
            latitude = -latitude;
        }

        Double longitude = this.longitude;
        if (longitude < 0) {
            ew = "W";
            longitude = -longitude;
        }

        int nsDegrees = latitude.intValue();
        int nsMinutes = (int) ((latitude - nsDegrees) * 60);
        Double nsSeconds = (latitude - nsDegrees - (double) nsMinutes / 60.0) * 3600;
        int ewDegrees = longitude.intValue();
        int ewMinutes = (int) ((longitude - ewDegrees) * 60);
        Double ewSeconds = (longitude - ewDegrees - (double) ewMinutes / 60.0) * 3600;

        if (format == LatitudeLongitudeFormat.COMPASS) {
            return String.format("%s%02d.%02d.%02d %s%02d.%02d.%02d",
                    ns, nsDegrees, nsMinutes, nsSeconds.intValue(), ew, ewDegrees, ewMinutes, ewSeconds.intValue());
        }

        return String.format("%02d:%02d'%f%s %02d:%02d'%f%s",
                nsDegrees, nsMinutes, nsSeconds, ns, ewDegrees, ewMinutes, ewSeconds, ew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, format);
    }
}
