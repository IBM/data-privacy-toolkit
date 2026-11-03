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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPatientAnimal;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPatientCommunication;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPatientContact;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRPatientLink;

import java.util.Collection;

/** FHIRPatient FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPatient extends FHIRBaseDomainResource {
    /** Constructs a FHIRPatient. */
    public FHIRPatient() {}


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
    public Collection<FHIRHumanName> getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(Collection<FHIRHumanName> name) {
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
     * Returns the deceasedBoolean.
     * @return the deceasedBoolean
     */
    public boolean isDeceasedBoolean() {
        return deceasedBoolean;
    }

    /**
     * Sets the deceasedBoolean.
     * @param deceasedBoolean the deceasedBoolean
     */
    public void setDeceasedBoolean(boolean deceasedBoolean) {
        this.deceasedBoolean = deceasedBoolean;
    }

    /**
     * Returns the deceasedDateTime.
     * @return the deceasedDateTime
     */
    public String getDeceasedDateTime() {
        return deceasedDateTime;
    }

    /**
     * Sets the deceasedDateTime.
     * @param deceasedDateTime the deceasedDateTime
     */
    public void setDeceasedDateTime(String deceasedDateTime) {
        this.deceasedDateTime = deceasedDateTime;
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
     * Returns the maritalStatus.
     * @return the maritalStatus
     */
    public FHIRCodeableConcept getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the maritalStatus.
     * @param maritalStatus the maritalStatus
     */
    public void setMaritalStatus(FHIRCodeableConcept maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Returns the multipleBirthBoolean.
     * @return the multipleBirthBoolean
     */
    public boolean isMultipleBirthBoolean() {
        return multipleBirthBoolean;
    }

    /**
     * Sets the multipleBirthBoolean.
     * @param multipleBirthBoolean the multipleBirthBoolean
     */
    public void setMultipleBirthBoolean(boolean multipleBirthBoolean) {
        this.multipleBirthBoolean = multipleBirthBoolean;
    }

    /**
     * Returns the multipleBirthInteger.
     * @return the multipleBirthInteger
     */
    public int getMultipleBirthInteger() {
        return multipleBirthInteger;
    }

    /**
     * Sets the multipleBirthInteger.
     * @param multipleBirthInteger the multipleBirthInteger
     */
    public void setMultipleBirthInteger(int multipleBirthInteger) {
        this.multipleBirthInteger = multipleBirthInteger;
    }

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
     * Returns the contact.
     * @return the contact
     */
    public Collection<FHIRPatientContact> getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     * @param contact the contact
     */
    public void setContact(Collection<FHIRPatientContact> contact) {
        this.contact = contact;
    }

    /**
     * Returns the careProvider.
     * @return the careProvider
     */
    public Collection<FHIRReference> getCareProvider() {
        return careProvider;
    }

    /**
     * Sets the careProvider.
     * @param careProvider the careProvider
     */
    public void setCareProvider(Collection<FHIRReference> careProvider) {
        this.careProvider = careProvider;
    }

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
     * Returns the animal.
     * @return the animal
     */
    public FHIRPatientAnimal getAnimal() {
        return animal;
    }

    /**
     * Sets the animal.
     * @param animal the animal
     */
    public void setAnimal(FHIRPatientAnimal animal) {
        this.animal = animal;
    }

    /**
     * Returns the communication.
     * @return the communication
     */
    public Collection<FHIRPatientCommunication> getCommunication() {
        return communication;
    }

    /**
     * Sets the communication.
     * @param communication the communication
     */
    public void setCommunication(Collection<FHIRPatientCommunication> communication) {
        this.communication = communication;
    }

    /**
     * Returns the link.
     * @return the link
     */
    public Collection<FHIRPatientLink> getLink() {
        return link;
    }

    /**
     * Sets the link.
     * @param link the link
     */
    public void setLink(Collection<FHIRPatientLink> link) {
        this.link = link;
    }

    private Collection<FHIRPatientLink> link;
    private Collection<FHIRPatientCommunication> communication;
    private FHIRPatientAnimal animal;
    private Collection<FHIRAttachment> photo;
    private Collection<FHIRIdentifier> identifier;
    private String resourceType;
    private boolean active;
    private Collection<FHIRHumanName> name;
    private Collection<FHIRContactPoint> telecom;
    private String gender;
    private String birthDate;
    private boolean deceasedBoolean;
    private String deceasedDateTime;
    private Collection<FHIRAddress> address;
    private FHIRCodeableConcept maritalStatus;
    private boolean multipleBirthBoolean;
    private int multipleBirthInteger;
    private Collection<FHIRPatientContact> contact;
    private Collection<FHIRReference> careProvider;
    private FHIRReference managingOrganization;

}
