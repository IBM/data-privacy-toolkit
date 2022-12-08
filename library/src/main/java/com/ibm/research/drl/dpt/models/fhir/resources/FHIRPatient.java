/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPatient extends FHIRBaseDomainResource {

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<FHIRHumanName> getName() {
        return name;
    }

    public void setName(Collection<FHIRHumanName> name) {
        this.name = name;
    }

    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
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

    public boolean isDeceasedBoolean() {
        return deceasedBoolean;
    }

    public void setDeceasedBoolean(boolean deceasedBoolean) {
        this.deceasedBoolean = deceasedBoolean;
    }

    public String getDeceasedDateTime() {
        return deceasedDateTime;
    }

    public void setDeceasedDateTime(String deceasedDateTime) {
        this.deceasedDateTime = deceasedDateTime;
    }

    public Collection<FHIRAddress> getAddress() {
        return address;
    }

    public void setAddress(Collection<FHIRAddress> address) {
        this.address = address;
    }

    public FHIRCodeableConcept getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(FHIRCodeableConcept maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isMultipleBirthBoolean() {
        return multipleBirthBoolean;
    }

    public void setMultipleBirthBoolean(boolean multipleBirthBoolean) {
        this.multipleBirthBoolean = multipleBirthBoolean;
    }

    public int getMultipleBirthInteger() {
        return multipleBirthInteger;
    }

    public void setMultipleBirthInteger(int multipleBirthInteger) {
        this.multipleBirthInteger = multipleBirthInteger;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public Collection<FHIRPatientContact> getContact() {
        return contact;
    }

    public void setContact(Collection<FHIRPatientContact> contact) {
        this.contact = contact;
    }

    public Collection<FHIRReference> getCareProvider() {
        return careProvider;
    }

    public void setCareProvider(Collection<FHIRReference> careProvider) {
        this.careProvider = careProvider;
    }

    public FHIRReference getManagingOrganization() {
        return managingOrganization;
    }

    public void setManagingOrganization(FHIRReference managingOrganization) {
        this.managingOrganization = managingOrganization;
    }


    public Collection<FHIRAttachment> getPhoto() {
        return photo;
    }

    public void setPhoto(Collection<FHIRAttachment> photo) {
        this.photo = photo;
    }

    public FHIRPatientAnimal getAnimal() {
        return animal;
    }

    public void setAnimal(FHIRPatientAnimal animal) {
        this.animal = animal;
    }

    public Collection<FHIRPatientCommunication> getCommunication() {
        return communication;
    }

    public void setCommunication(Collection<FHIRPatientCommunication> communication) {
        this.communication = communication;
    }

    public Collection<FHIRPatientLink> getLink() {
        return link;
    }

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
