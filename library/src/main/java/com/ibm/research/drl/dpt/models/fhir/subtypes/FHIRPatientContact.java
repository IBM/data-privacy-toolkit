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
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;

import java.util.Collection;

/** FHIRPatientContact FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPatientContact {
    /** Constructs a FHIRPatientContact. */
    public FHIRPatientContact() {}


    /**
     * Returns the relationship.
     * @return the relationship
     */
    public Collection<FHIRCodeableConcept> getRelationship() {
        return relationship;
    }

    /**
     * Sets the relationship.
     * @param relationship the relationship
     */
    public void setRelationship(Collection<FHIRCodeableConcept> relationship) {
        this.relationship = relationship;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public FHIRHumanName getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(FHIRHumanName name) {
        this.name = name;
    }

    /**
     * Returns the telecom.
     * @return the telecom
     */
    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    /**
     * Sets the telecom.
     * @param telecom the telecom
     */
    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    /**
     * Returns the address.
     * @return the address
     */
    public FHIRAddress getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param address the address
     */
    public void setAddress(FHIRAddress address) {
        this.address = address;
    }

    /**
     * Returns the gender.
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Returns the organization.
     * @return the organization
     */
    public FHIRReference getOrganization() {
        return organization;
    }

    /**
     * Sets the organization.
     * @param organization the organization
     */
    public void setOrganization(FHIRReference organization) {
        this.organization = organization;
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

    private Collection<FHIRCodeableConcept> relationship;
    private FHIRHumanName name;
    private Collection<FHIRContactPoint> telecom;
    private FHIRAddress address;
    private String gender;
    private FHIRReference organization;
    private FHIRPeriod period;
}
