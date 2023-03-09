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
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPractitionerQualification;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPractitionerRole;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPractitioner extends FHIRBaseDomainResource {

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String resourceType;
    private Collection<FHIRIdentifier> identifier;
    private boolean active;
    private FHIRHumanName name;
    private Collection<FHIRContactPoint> telecom;
    private Collection<FHIRAddress> address;
    private String gender;
    private String birthDate;
    private Collection<FHIRAttachment> photo;
    private Collection<FHIRPractitionerRole> practitionerRole;
    private Collection<FHIRPractitionerQualification> qualification;
    private Collection<FHIRCodeableConcept> communication;

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FHIRHumanName getName() {
        return name;
    }

    public void setName(FHIRHumanName name) {
        this.name = name;
    }

    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    public Collection<FHIRAddress> getAddress() {
        return address;
    }

    public void setAddress(Collection<FHIRAddress> address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Collection<FHIRAttachment> getPhoto() {
        return photo;
    }

    public void setPhoto(Collection<FHIRAttachment> photo) {
        this.photo = photo;
    }

    public Collection<FHIRPractitionerRole> getPractitionerRole() {
        return practitionerRole;
    }

    public void setPractitionerRole(Collection<FHIRPractitionerRole> practitionerRole) {
        this.practitionerRole = practitionerRole;
    }

    public Collection<FHIRPractitionerQualification> getQualification() {
        return qualification;
    }

    public void setQualification(Collection<FHIRPractitionerQualification> qualification) {
        this.qualification = qualification;
    }

    public Collection<FHIRCodeableConcept> getCommunication() {
        return communication;
    }

    public void setCommunication(Collection<FHIRCodeableConcept> communication) {
        this.communication = communication;
    }

}
