/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class PostalCode implements Location {
    private final String code;
    private final LatitudeLongitude latitudeLongitude;

    /**
     * Instantiates a new Postal code.
     *
     * @param code      the code
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public PostalCode(String code, Double latitude, Double longitude) {
        this.code = code;
        this.latitudeLongitude = new LatitudeLongitude(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    @Override
    public LatitudeLongitude getLocation() {
        return this.latitudeLongitude;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.code;
    }
}
