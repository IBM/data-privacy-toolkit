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

import com.ibm.research.drl.dpt.util.CountryNameSpecification;

import java.util.Arrays;

public class Country implements Location, LocalizedEntity {

    private final String name;
    private final String nameCountryCode;
    private final String iso2code;
    private final String iso3code;
    private final String continent;
    private final LatitudeLongitude latitudeLongitude;
    private Country[] neighbors;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return nameCountryCode;
    }

    /**
     * Instantiates a new Country.
     *
     * @param name            the name
     * @param iso2code        the iso 2 code
     * @param iso3code        the iso 3 code
     * @param continent       the continent
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @param nameCountryCode the name country code
     */
    public Country(String name, String iso2code, String iso3code, String continent, Double latitude, Double longitude, String nameCountryCode) {
        this.name = name;
        this.iso2code = iso2code;
        this.iso3code = iso3code;
        this.continent = continent;
        this.nameCountryCode = nameCountryCode;
        this.latitudeLongitude = new LatitudeLongitude(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Get neighbors country [ ].
     *
     * @return the country [ ]
     */
    public Country[] getNeighbors() {
        return neighbors;
    }

    /**
     * Sets neighbors.
     *
     * @param neighbors the neighbors
     */
    public void setNeighbors(final Country[] neighbors) {
        this.neighbors = Arrays.copyOf(neighbors, neighbors.length);
    }

    /**
     * Gets continent.
     *
     * @return the continent
     */
    public String getContinent() {
        return this.continent;
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
     * Gets name.
     *
     * @param spec the spec
     * @return the name
     */
    public String getName(CountryNameSpecification spec) {
        switch (spec) {
            case ISO2:
                return iso2code;
            case ISO3:
                return iso3code;
            case NAME:
            default:
                return name;
        }
    }
}
