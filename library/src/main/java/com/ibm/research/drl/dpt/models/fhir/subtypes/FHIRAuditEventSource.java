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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

/** FHIRAuditEventSource FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventSource {
    /** Constructs a FHIRAuditEventSource. */
    public FHIRAuditEventSource() {}


    private String site;
    private FHIRIdentifier identifier;
    private Collection<FHIRCoding> type;

    /**
     * Returns the type.
     * @return the type
     */
    public Collection<FHIRCoding> getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(Collection<FHIRCoding> type) {
        this.type = type;
    }

    /**
     * Returns the site.
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets the site.
     * @param site the site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

}


