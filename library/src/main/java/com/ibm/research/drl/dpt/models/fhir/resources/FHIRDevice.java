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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAnnotation;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

/** FHIRDevice FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDevice extends FHIRBaseDomainResource {
    /** Constructs a FHIRDevice. */
    public FHIRDevice() {}


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
     * Returns the type.
     * @return the type
     */
    public FHIRCodeableConcept getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    /**
     * Returns the note.
     * @return the note
     */
    public Collection<FHIRAnnotation> getNote() {
        return note;
    }

    /**
     * Sets the note.
     * @param note the note
     */
    public void setNote(Collection<FHIRAnnotation> note) {
        this.note = note;
    }

    /**
     * Returns the status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the manufacturer.
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer.
     * @param manufacturer the manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the model.
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model.
     * @param model the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the version.
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the manufactureDate.
     * @return the manufactureDate
     */
    public String getManufactureDate() {
        return manufactureDate;
    }

    /**
     * Sets the manufactureDate.
     * @param manufactureDate the manufactureDate
     */
    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    /**
     * Returns the expiry.
     * @return the expiry
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiry.
     * @param expiry the expiry
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * Returns the udi.
     * @return the udi
     */
    public String getUdi() {
        return udi;
    }

    /**
     * Sets the udi.
     * @param udi the udi
     */
    public void setUdi(String udi) {
        this.udi = udi;
    }

    /**
     * Returns the lotNumber.
     * @return the lotNumber
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * Sets the lotNumber.
     * @param lotNumber the lotNumber
     */
    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    /**
     * Returns the owner.
     * @return the owner
     */
    public FHIRReference getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     * @param owner the owner
     */
    public void setOwner(FHIRReference owner) {
        this.owner = owner;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public FHIRReference getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * @param location the location
     */
    public void setLocation(FHIRReference location) {
        this.location = location;
    }

    /**
     * Returns the patient.
     * @return the patient
     */
    public FHIRReference getPatient() {
        return patient;
    }

    /**
     * Sets the patient.
     * @param patient the patient
     */
    public void setPatient(FHIRReference patient) {
        this.patient = patient;
    }

    /**
     * Returns the contact.
     * @return the contact
     */
    public Collection<FHIRContactPoint> getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     * @param contact the contact
     */
    public void setContact(Collection<FHIRContactPoint> contact) {
        this.contact = contact;
    }

    /**
     * Returns the url.
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
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

    private String resourceType;

    private Collection<FHIRIdentifier> identifier;
    private FHIRCodeableConcept type;
    private Collection<FHIRAnnotation> note;
    private String status;
    private String manufacturer;
    private String model;
    private String version;
    private String manufactureDate;
    private String expiry;
    private String udi;
    private String lotNumber;
    private FHIRReference owner;
    private FHIRReference location;
    private FHIRReference patient;
    private Collection<FHIRContactPoint> contact;
    private String url;
}
