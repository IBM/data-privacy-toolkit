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

/** FHIRPractitioner FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPractitioner extends FHIRBaseDomainResource {
    /** Constructs a FHIRPractitioner. */
    public FHIRPractitioner() {}


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

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the active.
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     * @param active the active
     */
    public void setActive(boolean active) {
        this.active = active;
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
    public Collection<FHIRAddress> getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param address the address
     */
    public void setAddress(Collection<FHIRAddress> address) {
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
     * Returns the birthDate.
     * @return the birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birthDate.
     * @param birthDate the birthDate
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Returns the photo.
     * @return the photo
     */
    public Collection<FHIRAttachment> getPhoto() {
        return photo;
    }

    /**
     * Sets the photo.
     * @param photo the photo
     */
    public void setPhoto(Collection<FHIRAttachment> photo) {
        this.photo = photo;
    }

    /**
     * Returns the practitionerRole.
     * @return the practitionerRole
     */
    public Collection<FHIRPractitionerRole> getPractitionerRole() {
        return practitionerRole;
    }

    /**
     * Sets the practitionerRole.
     * @param practitionerRole the practitionerRole
     */
    public void setPractitionerRole(Collection<FHIRPractitionerRole> practitionerRole) {
        this.practitionerRole = practitionerRole;
    }

    /**
     * Returns the qualification.
     * @return the qualification
     */
    public Collection<FHIRPractitionerQualification> getQualification() {
        return qualification;
    }

    /**
     * Sets the qualification.
     * @param qualification the qualification
     */
    public void setQualification(Collection<FHIRPractitionerQualification> qualification) {
        this.qualification = qualification;
    }

    /**
     * Returns the communication.
     * @return the communication
     */
    public Collection<FHIRCodeableConcept> getCommunication() {
        return communication;
    }

    /**
     * Sets the communication.
     * @param communication the communication
     */
    public void setCommunication(Collection<FHIRCodeableConcept> communication) {
        this.communication = communication;
    }

}
