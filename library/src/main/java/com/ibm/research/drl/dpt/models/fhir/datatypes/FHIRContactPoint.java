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

/** FHIRContactPoint FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContactPoint {
    /** Constructs a FHIRContactPoint. */
    public FHIRContactPoint() {}

    /**
     * Returns the system.
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the system.
     * @param system the system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Returns the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
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
     * Returns the rank.
     * @return the rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Sets the rank.
     * @param rank the rank
     */
    public void setRank(String rank) {
        this.rank = rank;
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

    /* v1.0.2
        {
      "resourceType" : "ContactPoint",
      // from Element: extension
      "system" : "<code>", // C? phone | fax | email | pager | other
      "value" : "<string>", // The actual contact point details
      "use" : "<code>", // home | work | temp | old | mobile - purpose of this contact point
      "rank" : "<positiveInt>", // Specify preferred order of use (1 = highest)
      "period" : { Period } // Time period when the contact point was/is in use
    }
         */
    private String system;
    private String value;
    private String use;
    private String rank;
    private FHIRPeriod period;

    /**
     * Returns the extension.
     * @return the extension
     */
    public FHIRExtension getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     * @param extension the extension
     */
    public void setExtension(FHIRExtension extension) {
        this.extension = extension;
    }

    private FHIRExtension extension;
}
