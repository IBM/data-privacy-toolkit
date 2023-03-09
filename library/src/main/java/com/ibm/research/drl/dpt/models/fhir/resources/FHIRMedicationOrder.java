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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrder extends FHIRBaseDomainResource {

    public String getResourceType() {
        return resourceType;
    }

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

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getDateWritten() {
        return dateWritten;
    }

    public void setDateWritten(String dateWritten) {
        this.dateWritten = dateWritten;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateEnded() {
        return dateEnded;
    }

    public void setDateEnded(String dateEnded) {
        this.dateEnded = dateEnded;
    }

    public FHIRCodeableConcept getReasonEnded() {
        return reasonEnded;
    }

    public void setReasonEnded(FHIRCodeableConcept reasonEnded) {
        this.reasonEnded = reasonEnded;
    }

    public FHIRReference getPatient() {
        return patient;
    }

    public void setPatient(FHIRReference patient) {
        this.patient = patient;
    }

    public FHIRReference getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(FHIRReference prescriber) {
        this.prescriber = prescriber;
    }

    public FHIRReference getEncounter() {
        return encounter;
    }

    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    public FHIRCodeableConcept getReasonCodeableConcept() {
        return reasonCodeableConcept;
    }

    public void setReasonCodeableConcept(FHIRCodeableConcept reasonCodeableConcept) {
        this.reasonCodeableConcept = reasonCodeableConcept;
    }

    public FHIRReference getReasonReference() {
        return reasonReference;
    }

    public void setReasonReference(FHIRReference reasonReference) {
        this.reasonReference = reasonReference;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FHIRCodeableConcept getMedicationCodeableConcept() {
        return medicationCodeableConcept;
    }

    public void setMedicationCodeableConcept(FHIRCodeableConcept medicationCodeableConcept) {
        this.medicationCodeableConcept = medicationCodeableConcept;
    }

    public FHIRReference getMedicationReference() {
        return medicationReference;
    }

    public void setMedicationReference(FHIRReference medicationReference) {
        this.medicationReference = medicationReference;
    }

    public Collection<FHIRMedicationOrderDosageInstruction> getDosageInstruction() {
        return dosageInstruction;
    }

    public void setDosageInstruction(Collection<FHIRMedicationOrderDosageInstruction> dosageInstruction) {
        this.dosageInstruction = dosageInstruction;
    }

    public FHIRMedicationOrderDispenseRequest getDispenseRequest() {
        return dispenseRequest;
    }

    public void setDispenseRequest(FHIRMedicationOrderDispenseRequest dispenseRequest) {
        this.dispenseRequest = dispenseRequest;
    }

    public FHIRMedicationOrderSubstitution getSubstitution() {
        return substitution;
    }

    public void setSubstitution(FHIRMedicationOrderSubstitution substitution) {
        this.substitution = substitution;
    }

    public FHIRReference getPriorPrescription() {
        return priorPrescription;
    }

    public void setPriorPrescription(FHIRReference priorPrescription) {
        this.priorPrescription = priorPrescription;
    }

}
