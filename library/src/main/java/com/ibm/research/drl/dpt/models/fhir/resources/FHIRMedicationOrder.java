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
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationOrderDispenseRequest;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationOrderDosageInstruction;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationOrderSubstitution;

import java.util.Collection;

/** FHIRMedicationOrder FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrder extends FHIRBaseDomainResource {
    /** Constructs a FHIRMedicationOrder. */
    public FHIRMedicationOrder() {}


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
    private String dateWritten;
    private String status;
    private String dateEnded;
    private FHIRCodeableConcept reasonEnded;
    private FHIRReference patient;
    private FHIRReference prescriber;
    private FHIRReference encounter;
    private FHIRCodeableConcept reasonCodeableConcept;
    private FHIRReference reasonReference;
    private String note;
    private FHIRCodeableConcept medicationCodeableConcept;
    private FHIRReference medicationReference;
    private Collection<FHIRMedicationOrderDosageInstruction> dosageInstruction;
    private FHIRMedicationOrderDispenseRequest dispenseRequest;
    private FHIRMedicationOrderSubstitution substitution;
    private FHIRReference priorPrescription;

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
     * Returns the dateWritten.
     * @return the dateWritten
     */
    public String getDateWritten() {
        return dateWritten;
    }

    /**
     * Sets the dateWritten.
     * @param dateWritten the dateWritten
     */
    public void setDateWritten(String dateWritten) {
        this.dateWritten = dateWritten;
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
     * Returns the dateEnded.
     * @return the dateEnded
     */
    public String getDateEnded() {
        return dateEnded;
    }

    /**
     * Sets the dateEnded.
     * @param dateEnded the dateEnded
     */
    public void setDateEnded(String dateEnded) {
        this.dateEnded = dateEnded;
    }

    /**
     * Returns the reasonEnded.
     * @return the reasonEnded
     */
    public FHIRCodeableConcept getReasonEnded() {
        return reasonEnded;
    }

    /**
     * Sets the reasonEnded.
     * @param reasonEnded the reasonEnded
     */
    public void setReasonEnded(FHIRCodeableConcept reasonEnded) {
        this.reasonEnded = reasonEnded;
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
     * Returns the prescriber.
     * @return the prescriber
     */
    public FHIRReference getPrescriber() {
        return prescriber;
    }

    /**
     * Sets the prescriber.
     * @param prescriber the prescriber
     */
    public void setPrescriber(FHIRReference prescriber) {
        this.prescriber = prescriber;
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
     * Returns the reasonCodeableConcept.
     * @return the reasonCodeableConcept
     */
    public FHIRCodeableConcept getReasonCodeableConcept() {
        return reasonCodeableConcept;
    }

    /**
     * Sets the reasonCodeableConcept.
     * @param reasonCodeableConcept the reasonCodeableConcept
     */
    public void setReasonCodeableConcept(FHIRCodeableConcept reasonCodeableConcept) {
        this.reasonCodeableConcept = reasonCodeableConcept;
    }

    /**
     * Returns the reasonReference.
     * @return the reasonReference
     */
    public FHIRReference getReasonReference() {
        return reasonReference;
    }

    /**
     * Sets the reasonReference.
     * @param reasonReference the reasonReference
     */
    public void setReasonReference(FHIRReference reasonReference) {
        this.reasonReference = reasonReference;
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
     * Returns the dosageInstruction.
     * @return the dosageInstruction
     */
    public Collection<FHIRMedicationOrderDosageInstruction> getDosageInstruction() {
        return dosageInstruction;
    }

    /**
     * Sets the dosageInstruction.
     * @param dosageInstruction the dosageInstruction
     */
    public void setDosageInstruction(Collection<FHIRMedicationOrderDosageInstruction> dosageInstruction) {
        this.dosageInstruction = dosageInstruction;
    }

    /**
     * Returns the dispenseRequest.
     * @return the dispenseRequest
     */
    public FHIRMedicationOrderDispenseRequest getDispenseRequest() {
        return dispenseRequest;
    }

    /**
     * Sets the dispenseRequest.
     * @param dispenseRequest the dispenseRequest
     */
    public void setDispenseRequest(FHIRMedicationOrderDispenseRequest dispenseRequest) {
        this.dispenseRequest = dispenseRequest;
    }

    /**
     * Returns the substitution.
     * @return the substitution
     */
    public FHIRMedicationOrderSubstitution getSubstitution() {
        return substitution;
    }

    /**
     * Sets the substitution.
     * @param substitution the substitution
     */
    public void setSubstitution(FHIRMedicationOrderSubstitution substitution) {
        this.substitution = substitution;
    }

    /**
     * Returns the priorPrescription.
     * @return the priorPrescription
     */
    public FHIRReference getPriorPrescription() {
        return priorPrescription;
    }

    /**
     * Sets the priorPrescription.
     * @param priorPrescription the priorPrescription
     */
    public void setPriorPrescription(FHIRReference priorPrescription) {
        this.priorPrescription = priorPrescription;
    }

}
