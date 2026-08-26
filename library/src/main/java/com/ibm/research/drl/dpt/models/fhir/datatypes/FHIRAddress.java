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
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;

import java.util.Collection;

/** FHIR Address datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
/** FHIRAddress FHIR datatype. */
public class FHIRAddress {
    /** Constructs a FHIRAddress. */
    public FHIRAddress() {}

    /* v1.0.2
    {doco
  "resourceType" : "Address",
  // from Element: extension
  "use" : "<code>", // home | work | temp | old - purpose of this address
  "type" : "<code>", // postal | physical | both
  "text" : "<string>", // Text representation of the address
  "line" : ["<string>"], // Street name, number, direction & P.O. Box etc.
  "city" : "<string>", // Name of city, town etc.
  "district" : "<string>", // District name (aka county)
  "state" : "<string>", // Sub-unit of country (abbreviations ok)
  "postalCode" : "<string>", // Postal code for area
  "country" : "<string>", // Country (can be ISO 3166 3 letter code)
            "period" : { Period } // Time period when address was/is in use
        }
     */


    /** Returns the extension. @return the extension */
    public FHIRExtension getExtension() {
        return extension;
    }
    /** Sets the extension to set. @param extension the extension to set */
    public void setExtension(FHIRExtension extension) {
        this.extension = extension;
    }

    /** Returns the resource type. @return the resource type */
    public String getResourceType() {
        return resourceType;
    }
    /** Sets the resource type to set. @param resourceType the resource type to set */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /** Returns the use. @return the use */
    public String getUse() {
        return use;
    }
    /** Sets the use to set. @param use the use to set */
    public void setUse(String use) {
        this.use = use;
    }

    /** Returns the type. @return the type */
    public String getType() {
        return type;
    }
    /** Sets the type to set. @param type the type to set */
    public void setType(String type) {
        this.type = type;
    }

    /** Returns the text. @return the text */
    public String getText() {
        return text;
    }
    /** Sets the text to set. @param text the text to set */
    public void setText(String text) {
        this.text = text;
    }

    /** Returns the address lines. @return the address lines */
    public Collection<String> getLine() {
        return line;
    }
    /** Sets the address lines to set. @param line the address lines to set */
    public void setLine(Collection<String> line) {
        this.line = line;
    }

    /** Returns the city. @return the city */
    public String getCity() {
        return city;
    }
    /** Sets the city to set. @param city the city to set */
    public void setCity(String city) {
        this.city = city;
    }

    /** Returns the district. @return the district */
    public String getDistrict() {
        return district;
    }
    /** Sets the district to set. @param district the district to set */
    public void setDistrict(String district) {
        this.district = district;
    }

    /** Returns the state. @return the state */
    public String getState() {
        return state;
    }
    /** Sets the state to set. @param state the state to set */
    public void setState(String state) {
        this.state = state;
    }

    /** Returns the postal code. @return the postal code */
    public String getPostalCode() {
        return postalCode;
    }
    /** Sets the postal code to set. @param postalCode the postal code to set */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** Returns the country. @return the country */
    public String getCountry() {
        return country;
    }
    /** Sets the country to set. @param country the country to set */
    public void setCountry(String country) {
        this.country = country;
    }

    /** Returns the period. @return the period */
    public FHIRPeriod getPeriod() {
        return period;
    }
    /** Sets the period to set. @param period the period to set */
    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    private FHIRExtension extension;
    private String resourceType;
    private String use;
    private String type;
    private String text;
    private Collection<String> line;
    private String city;
    private String district;
    private String state;
    private String postalCode;
    private String country;
    private FHIRPeriod period;
}
