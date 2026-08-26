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

import java.util.List;

/**
 * Represents a continent with its geographic location.
 */
public class Continent implements Location, LocalizedEntity {

    /** The continent name. */
    private final String name;
    /** The country code this name belongs to. */
    private final String nameCountryCode;
    /** The geographic coordinates. */
    private final LatitudeLongitude latitudeLongitude;
    /** Nearby continents. */
    private List<Continent> neighbors;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    @Override
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
