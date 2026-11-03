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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationAdministrationDosage;

import java.util.Collection;

/** FHIRMedicationAdministration FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationAdministration extends FHIRBaseDomainResource {
    /** Constructs a FHIRMedicationAdministration. */
    public FHIRMedicationAdministration() {}


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
    private String status;
    private FHIRReference patient;
    private FHIRReference practitioner;
    private FHIRReference encounter;
    private FHIRReference prescription;
    private boolean wasNotGiven;
    private Collection<FHIRCodeableConcept> reasonNotGiven;
    private Collection<FHIRCodeableConcept> reasonGiven;
    private String effectiveTimeDateTime;
    private FHIRPeriod effectiveTimePeriod;
    private FHIRCodeableConcept medicationCodeableConcept;
    private FHIRReference medicationReference;
    private Collection<FHIRReference> device;
    private String note;
    private FHIRMedicationAdministrationDosage dosage;

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
     * Returns the practitioner.
     * @return the practitioner
     */
    public FHIRReference getPractitioner() {
        return practitioner;
    }

    /**
     * Sets the practitioner.
     * @param practitioner the practitioner
     */
    public void setPractitioner(FHIRReference practitioner) {
        this.practitioner = practitioner;
    }

    /**
     * Returns the encounter.
     * @return the encounter
     */
    public FHIRReference getEncounter() {
        return encounter;
    }

    /**
     * Sets the encounter.
     * @param encounter the encounter
     */
    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    /**
     * Returns the prescription.
     * @return the prescription
     */
    public FHIRReference getPrescription() {
        return prescription;
    }

    /**
     * Sets the prescription.
     * @param prescription the prescription
     */
    public void setPrescription(FHIRReference prescription) {
        this.prescription = prescription;
    }

    /**
     * Returns the wasNotGiven.
     * @return the wasNotGiven
     */
    public boolean isWasNotGiven() {
        return wasNotGiven;
    }

    /**
     * Sets the wasNotGiven.
     * @param wasNotGiven the wasNotGiven
     */
    public void setWasNotGiven(boolean wasNotGiven) {
        this.wasNotGiven = wasNotGiven;
    }

    /**
     * Returns the reasonNotGiven.
     * @return the reasonNotGiven
     */
    public Collection<FHIRCodeableConcept> getReasonNotGiven() {
        return reasonNotGiven;
    }

    /**
     * Sets the reasonNotGiven.
     * @param reasonNotGiven the reasonNotGiven
     */
    public void setReasonNotGiven(Collection<FHIRCodeableConcept> reasonNotGiven) {
        this.reasonNotGiven = reasonNotGiven;
    }

    /**
     * Returns the reasonGiven.
     * @return the reasonGiven
     */
    public Collection<FHIRCodeableConcept> getReasonGiven() {
        return reasonGiven;
    }

    /**
     * Sets the reasonGiven.
     * @param reasonGiven the reasonGiven
     */
    public void setReasonGiven(Collection<FHIRCodeableConcept> reasonGiven) {
        this.reasonGiven = reasonGiven;
    }

    /**
     * Returns the effectiveTimeDateTime.
     * @return the effectiveTimeDateTime
     */
    public String getEffectiveTimeDateTime() {
        return effectiveTimeDateTime;
    }

    /**
     * Sets the effectiveTimeDateTime.
     * @param effectiveTimeDateTime the effectiveTimeDateTime
     */
    public void setEffectiveTimeDateTime(String effectiveTimeDateTime) {
        this.effectiveTimeDateTime = effectiveTimeDateTime;
    }

    /**
     * Returns the effectiveTimePeriod.
     * @return the effectiveTimePeriod
     */
    public FHIRPeriod getEffectiveTimePeriod() {
        return effectiveTimePeriod;
    }

    /**
     * Sets the effectiveTimePeriod.
     * @param effectiveTimePeriod the effectiveTimePeriod
     */
    public void setEffectiveTimePeriod(FHIRPeriod effectiveTimePeriod) {
        this.effectiveTimePeriod = effectiveTimePeriod;
    }

    /**
     * Returns the medicationCodeableConcept.
     * @return the medicationCodeableConcept
     */
    public FHIRCodeableConcept getMedicationCodeableConcept() {
        return medicationCodeableConcept;
    }

    /**
     * Sets the medicationCodeableConcept.
     * @param medicationCodeableConcept the medicationCodeableConcept
     */
    public void setMedicationCodeableConcept(FHIRCodeableConcept medicationCodeableConcept) {
        this.medicationCodeableConcept = medicationCodeableConcept;
    }

    /**
     * Returns the medicationReference.
     * @return the medicationReference
     */
    public FHIRReference getMedicationReference() {
        return medicationReference;
    }

    /**
     * Sets the medicationReference.
     * @param medicationReference the medicationReference
     */
    public void setMedicationReference(FHIRReference medicationReference) {
        this.medicationReference = medicationReference;
    }

    /**
     * Returns the device.
     * @return the device
     */
    public Collection<FHIRReference> getDevice() {
        return device;
    }

    /**
     * Sets the device.
     * @param device the device
     */
    public void setDevice(Collection<FHIRReference> device) {
        this.device = device;
    }

    /**
     * Returns the note.
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note.
     * @param note the note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Returns the dosage.
     * @return the dosage
     */
    public FHIRMedicationAdministrationDosage getDosage() {
        return dosage;
    }

    /**
     * Sets the dosage.
     * @param dosage the dosage
     */
    public void setDosage(FHIRMedicationAdministrationDosage dosage) {
        this.dosage = dosage;
    }

}
