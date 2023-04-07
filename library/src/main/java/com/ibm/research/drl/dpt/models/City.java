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
    @Override
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
