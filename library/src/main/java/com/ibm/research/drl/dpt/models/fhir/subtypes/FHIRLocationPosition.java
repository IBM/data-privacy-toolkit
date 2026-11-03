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
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/** FHIRLocationPosition FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRLocationPosition {
    /** Constructs a FHIRLocationPosition. */
    public FHIRLocationPosition() {}

    /**
     * Returns the longitude.
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     * @param longitude the longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the latitude.
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     * @param latitude the latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the altitude.
     * @return the altitude
     */
    public float getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude.
     * @param altitude the altitude
     */
    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    private float longitude;
    private float latitude;
    private float altitude;
}


