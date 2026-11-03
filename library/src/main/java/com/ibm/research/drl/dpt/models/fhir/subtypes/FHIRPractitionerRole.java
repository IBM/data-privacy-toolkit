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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;

import java.util.Collection;


/** FHIRPractitionerRole FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPractitionerRole {
    /** Constructs a FHIRPractitionerRole. */
    public FHIRPractitionerRole() {}


    private FHIRReference managingOrganization;
    private FHIRCodeableConcept role;
    private Collection<FHIRCodeableConcept> specialty;
    private FHIRPeriod period;
    private Collection<FHIRReference> location;
    private Collection<FHIRReference> healthcareService;

    /**
     * Returns the managingOrganization.
     * @return the managingOrganization
     */
    public FHIRReference getManagingOrganization() {
        return managingOrganization;
    }

    /**
     * Sets the managingOrganization.
     * @param managingOrganization the managingOrganization
     */
    public void setManagingOrganization(FHIRReference managingOrganization) {
        this.managingOrganization = managingOrganization;
    }

    /**
     * Returns the role.
     * @return the role
     */
    public FHIRCodeableConcept getRole() {
        return role;
    }

    /**
     * Sets the role.
     * @param role the role
     */
    public void setRole(FHIRCodeableConcept role) {
        this.role = role;
    }

    /**
     * Returns the specialty.
     * @return the specialty
     */
    public Collection<FHIRCodeableConcept> getSpecialty() {
        return specialty;
    }

    /**
     * Sets the specialty.
     * @param specialty the specialty
     */
    public void setSpecialty(Collection<FHIRCodeableConcept> specialty) {
        this.specialty = specialty;
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
     * Returns the location.
     * @return the location
     */
    public Collection<FHIRReference> getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * @param location the location
     */
    public void setLocation(Collection<FHIRReference> location) {
        this.location = location;
    }

    /**
     * Returns the healthcareService.
     * @return the healthcareService
     */
    public Collection<FHIRReference> getHealthcareService() {
        return healthcareService;
    }

    /**
     * Sets the healthcareService.
     * @param healthcareService the healthcareService
     */
    public void setHealthcareService(Collection<FHIRReference> healthcareService) {
        this.healthcareService = healthcareService;
    }

}


