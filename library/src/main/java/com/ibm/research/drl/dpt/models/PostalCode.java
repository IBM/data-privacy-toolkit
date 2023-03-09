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
