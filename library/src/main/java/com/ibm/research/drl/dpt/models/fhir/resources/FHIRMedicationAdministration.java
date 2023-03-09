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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationAdministration extends FHIRBaseDomainResource {

    public String getResourceType() {
        return resourceType;
    }

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

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FHIRReference getPatient() {
        return patient;
    }

    public void setPatient(FHIRReference patient) {
        this.patient = patient;
    }

    public FHIRReference getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(FHIRReference practitioner) {
        this.practitioner = practitioner;
    }

    public FHIRReference getEncounter() {
        return encounter;
    }

    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    public FHIRReference getPrescription() {
        return prescription;
    }

    public void setPrescription(FHIRReference prescription) {
        this.prescription = prescription;
    }

    public boolean isWasNotGiven() {
        return wasNotGiven;
    }

    public void setWasNotGiven(boolean wasNotGiven) {
        this.wasNotGiven = wasNotGiven;
    }

    public Collection<FHIRCodeableConcept> getReasonNotGiven() {
        return reasonNotGiven;
    }

    public void setReasonNotGiven(Collection<FHIRCodeableConcept> reasonNotGiven) {
        this.reasonNotGiven = reasonNotGiven;
    }

    public Collection<FHIRCodeableConcept> getReasonGiven() {
        return reasonGiven;
    }

    public void setReasonGiven(Collection<FHIRCodeableConcept> reasonGiven) {
        this.reasonGiven = reasonGiven;
    }

    public String getEffectiveTimeDateTime() {
        return effectiveTimeDateTime;
    }

    public void setEffectiveTimeDateTime(String effectiveTimeDateTime) {
        this.effectiveTimeDateTime = effectiveTimeDateTime;
    }

    public FHIRPeriod getEffectiveTimePeriod() {
        return effectiveTimePeriod;
    }

    public void setEffectiveTimePeriod(FHIRPeriod effectiveTimePeriod) {
        this.effectiveTimePeriod = effectiveTimePeriod;
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

    public Collection<FHIRReference> getDevice() {
        return device;
    }

    public void setDevice(Collection<FHIRReference> device) {
        this.device = device;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FHIRMedicationAdministrationDosage getDosage() {
        return dosage;
    }

    public void setDosage(FHIRMedicationAdministrationDosage dosage) {
        this.dosage = dosage;
    }

}
