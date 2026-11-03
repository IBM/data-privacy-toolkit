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

/** FHIRHumanName FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRHumanName {
    /** Constructs a FHIRHumanName. */
    public FHIRHumanName() {}

    /* v1.0.2
    {
        "resourceType" : "HumanName",
        // from Element: extension
        "use" : "<code>", // usual | official | temp | nickname | anonymous | old | maiden
        "text" : "<string>", // Text representation of the full name
        "family" : ["<string>"], // Family name (often called 'Surname')
        "given" : ["<string>"], // Given names (not always 'first'). Includes middle names
        "prefix" : ["<string>"], // Parts that come before the name
        "suffix" : ["<string>"], // Parts that come after the name
        "period" : { Period } // Time period when name was/is in use
    }
     */

    /**
     * Returns the resourceType.
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resourceType.
     * @param resourceType the resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Returns the use.
     * @return the use
     */
    public String getUse() {
        return use;
    }

    /**
     * Sets the use.
     * @param use the use
     */
    public void setUse(String use) {
        this.use = use;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the family.
     * @return the family
     */
    public Collection<String> getFamily() {
        return family;
    }

    /**
     * Sets the family.
     * @param family the family
     */
    public void setFamily(Collection<String> family) {
        this.family = family;
    }

    /**
     * Returns the given.
     * @return the given
     */
    public Collection<String> getGiven() {
        return given;
    }

    /**
     * Sets the given.
     * @param given the given
     */
    public void setGiven(Collection<String> given) {
        this.given = given;
    }

    /**
     * Returns the prefix.
     * @return the prefix
     */
    public Collection<String> getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     * @param prefix the prefix
     */
    public void setPrefix(Collection<String> prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the suffix.
     * @return the suffix
     */
    public Collection<String> getSuffix() {
        return suffix;
    }

    /**
     * Sets the suffix.
     * @param suffix the suffix
     */
    public void setSuffix(Collection<String> suffix) {
        this.suffix = suffix;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public FHIRPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    /**
     * Returns the extension.
     * @return the extension
     */
    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     * @param extension the extension
     */
    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    private Collection<FHIRExtension> extension;
    private String resourceType;
    private String use;
    private String text;
    private Collection<String> family;
    private Collection<String> given;
    private Collection<String> prefix;
    private Collection<String> suffix;
    private FHIRPeriod period;

}
