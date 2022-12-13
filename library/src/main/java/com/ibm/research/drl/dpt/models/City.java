/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.util.List;

public class City implements Location, LocalizedEntity {

    private final String name;
    private final String nameCountryCode;
    private final String countryCode;
    private final LatitudeLongitude latitudeLongitude;
    private List<City> neighbors;

    /**
     * Instantiates a new City.
     *
     * @param name            the name
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @param countryCode     the country code
     * @param nameCountryCode the name country code
     */
    public City(String name, Double latitude, Double longitude, String countryCode, String nameCountryCode) {
        this.name = name;
        this.countryCode = countryCode;
        this.nameCountryCode = nameCountryCode;

        this.latitudeLongitude = new LatitudeLongitude(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return this.nameCountryCode;
    }

    /**
     * Gets neighbors.
     *
     * @return the neighbors
     */
    public List<City> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets neighbors.
     *
     * @param neighbors the neighbors
     */
    public void setNeighbors(List<City> neighbors) {
        this.neighbors = neighbors;
    }

    public LatitudeLongitude getLocation() {
        return this.latitudeLongitude;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return this.countryCode;
    }
}
