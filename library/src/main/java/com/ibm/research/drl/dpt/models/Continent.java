/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.util.List;

public class Continent implements Location, LocalizedEntity {

    private final String name;
    private final String nameCountryCode;
    private final LatitudeLongitude latitudeLongitude;
    private List<Continent> neighbors;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return nameCountryCode;
    }


    /**
     * Instantiates a new Continent.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param latitude        the latitude
     * @param longitude       the longitude
     */
    public Continent(String name, String nameCountryCode, Double latitude, Double longitude) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.latitudeLongitude = new LatitudeLongitude(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Gets neighbors.
     *
     * @return the neighbors
     */
    public List<Continent> getNeighbors() {
        return neighbors;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets neighbors.
     *
     * @param neighbors the neighbors
     */
    public void setNeighbors(List<Continent> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public LatitudeLongitude getLocation() {
        return this.latitudeLongitude;
    }
}
