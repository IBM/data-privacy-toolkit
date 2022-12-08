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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAnnotation;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRGoalOutcome;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGoal extends FHIRBaseDomainResource {

    private Collection<FHIRIdentifier> identifier;
    private FHIRReference subject;
    private String startDate;
    private FHIRCodeableConcept startCodeableConcept;
    private String targetDate;
    private FHIRQuantity targetQuantity;
    private Collection<FHIRCodeableConcept> category;
    private String description;
    private String status;
    private String statusDate;
    private FHIRCodeableConcept statusReason;
    private FHIRReference author;
    private FHIRCodeableConcept priority;
    private Collection<FHIRReference> addresses;
    private Collection<FHIRAnnotation> note;
    private Collection<FHIRGoalOutcome> outcome;

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public FHIRReference getSubject() {
        return subject;
    }

    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public FHIRCodeableConcept getStartCodeableConcept() {
        return startCodeableConcept;
    }

    public void setStartCodeableConcept(FHIRCodeableConcept startCodeableConcept) {
        this.startCodeableConcept = startCodeableConcept;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public FHIRQuantity getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(FHIRQuantity targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public Collection<FHIRCodeableConcept> getCategory() {
        return category;
    }

    public void setCategory(Collection<FHIRCodeableConcept> category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public FHIRCodeableConcept getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(FHIRCodeableConcept statusReason) {
        this.statusReason = statusReason;
    }

    public FHIRReference getAuthor() {
        return author;
    }

    public void setAuthor(FHIRReference author) {
        this.author = author;
    }

    public FHIRCodeableConcept getPriority() {
        return priority;
    }

    public void setPriority(FHIRCodeableConcept priority) {
        this.priority = priority;
    }

    public Collection<FHIRReference> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<FHIRReference> addresses) {
        this.addresses = addresses;
    }

    public Collection<FHIRAnnotation> getNote() {
        return note;
    }

    public void setNote(Collection<FHIRAnnotation> note) {
        this.note = note;
    }

    public Collection<FHIRGoalOutcome> getOutcome() {
        return outcome;
    }

    public void setOutcome(Collection<FHIRGoalOutcome> outcome) {
        this.outcome = outcome;
    }
}
