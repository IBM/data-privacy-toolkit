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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

/** FHIRAuditEventObject FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventObject {
    /** Constructs a FHIRAuditEventObject. */
    public FHIRAuditEventObject() {}


    private FHIRIdentifier identifier;
    private FHIRReference reference;
    private FHIRCoding type;
    private FHIRCoding role;
    private FHIRCoding lifecycle;
    private Collection<FHIRCoding> securityLabel;
    private String name;
    private String description;
    private String query;
    private Collection<FHIRAuditEventObjectDetail> detail;

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

    /**
     * Returns the reference.
     * @return the reference
     */
    public FHIRReference getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     * @param reference the reference
     */
    public void setReference(FHIRReference reference) {
        this.reference = reference;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public FHIRCoding getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(FHIRCoding type) {
        this.type = type;
    }

    /**
     * Returns the role.
     * @return the role
     */
    public FHIRCoding getRole() {
        return role;
    }

    /**
     * Sets the role.
     * @param role the role
     */
    public void setRole(FHIRCoding role) {
        this.role = role;
    }

    /**
     * Returns the lifecycle.
     * @return the lifecycle
     */
    public FHIRCoding getLifecycle() {
        return lifecycle;
    }

    /**
     * Sets the lifecycle.
     * @param lifecycle the lifecycle
     */
    public void setLifecycle(FHIRCoding lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * Returns the securityLabel.
     * @return the securityLabel
     */
    public Collection<FHIRCoding> getSecurityLabel() {
        return securityLabel;
    }

    /**
     * Sets the securityLabel.
     * @param securityLabel the securityLabel
     */
    public void setSecurityLabel(Collection<FHIRCoding> securityLabel) {
        this.securityLabel = securityLabel;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the query.
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     * @param query the query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Returns the detail.
     * @return the detail
     */
    public Collection<FHIRAuditEventObjectDetail> getDetail() {
        return detail;
    }

    /**
     * Sets the detail.
     * @param detail the detail
     */
    public void setDetail(Collection<FHIRAuditEventObjectDetail> detail) {
        this.detail = detail;
    }
}


