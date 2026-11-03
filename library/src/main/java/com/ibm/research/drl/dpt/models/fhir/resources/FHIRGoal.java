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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRGoalOutcome;

import java.util.Collection;


/** FHIRGoal FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGoal extends FHIRBaseDomainResource {
    /** Constructs a FHIRGoal. */
    public FHIRGoal() {}


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
     * Returns the subject.
     * @return the subject
     */
    public FHIRReference getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     * @param subject the subject
     */
    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    /**
     * Returns the startDate.
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the startDate.
     * @param startDate the startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the startCodeableConcept.
     * @return the startCodeableConcept
     */
    public FHIRCodeableConcept getStartCodeableConcept() {
        return startCodeableConcept;
    }

    /**
     * Sets the startCodeableConcept.
     * @param startCodeableConcept the startCodeableConcept
     */
    public void setStartCodeableConcept(FHIRCodeableConcept startCodeableConcept) {
        this.startCodeableConcept = startCodeableConcept;
    }

    /**
     * Returns the targetDate.
     * @return the targetDate
     */
    public String getTargetDate() {
        return targetDate;
    }

    /**
     * Sets the targetDate.
     * @param targetDate the targetDate
     */
    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * Returns the targetQuantity.
     * @return the targetQuantity
     */
    public FHIRQuantity getTargetQuantity() {
        return targetQuantity;
    }

    /**
     * Sets the targetQuantity.
     * @param targetQuantity the targetQuantity
     */
    public void setTargetQuantity(FHIRQuantity targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    /**
     * Returns the category.
     * @return the category
     */
    public Collection<FHIRCodeableConcept> getCategory() {
        return category;
    }

    /**
     * Sets the category.
     * @param category the category
     */
    public void setCategory(Collection<FHIRCodeableConcept> category) {
        this.category = category;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Returns the statusDate.
     * @return the statusDate
     */
    public String getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the statusDate.
     * @param statusDate the statusDate
     */
    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Returns the statusReason.
     * @return the statusReason
     */
    public FHIRCodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * Sets the statusReason.
     * @param statusReason the statusReason
     */
    public void setStatusReason(FHIRCodeableConcept statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * Returns the author.
     * @return the author
     */
    public FHIRReference getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     * @param author the author
     */
    public void setAuthor(FHIRReference author) {
        this.author = author;
    }

    /**
     * Returns the priority.
     * @return the priority
     */
    public FHIRCodeableConcept getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     * @param priority the priority
     */
    public void setPriority(FHIRCodeableConcept priority) {
        this.priority = priority;
    }

    /**
     * Returns the addresses.
     * @return the addresses
     */
    public Collection<FHIRReference> getAddresses() {
        return addresses;
    }

    /**
     * Sets the addresses.
     * @param addresses the addresses
     */
    public void setAddresses(Collection<FHIRReference> addresses) {
        this.addresses = addresses;
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
     * Returns the outcome.
     * @return the outcome
     */
    public Collection<FHIRGoalOutcome> getOutcome() {
        return outcome;
    }

    /**
     * Sets the outcome.
     * @param outcome the outcome
     */
    public void setOutcome(Collection<FHIRGoalOutcome> outcome) {
        this.outcome = outcome;
    }
}
