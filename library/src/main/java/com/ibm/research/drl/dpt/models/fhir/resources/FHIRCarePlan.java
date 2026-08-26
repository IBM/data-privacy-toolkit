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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRCarePlanActivity;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRCarePlanParticipant;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRCarePlanRelatedPlan;

import java.util.Collection;

/** FHIRCarePlan FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlan extends FHIRBaseDomainResource {
    /** Constructs a FHIRCarePlan. */
    public FHIRCarePlan() {}


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
     * Returns the context.
     * @return the context
     */
    public FHIRReference getContext() {
        return context;
    }

    /**
     * Sets the context.
     * @param context the context
     */
    public void setContext(FHIRReference context) {
        this.context = context;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public FHIRPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    /**
     * Returns the author.
     * @return the author
     */
    public Collection<FHIRReference> getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     * @param author the author
     */
    public void setAuthor(Collection<FHIRReference> author) {
        this.author = author;
    }

    /**
     * Returns the modified.
     * @return the modified
     */
    public String getModified() {
        return modified;
    }

    /**
     * Sets the modified.
     * @param modified the modified
     */
    public void setModified(String modified) {
        this.modified = modified;
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
     * Returns the support.
     * @return the support
     */
    public Collection<FHIRReference> getSupport() {
        return support;
    }

    /**
     * Sets the support.
     * @param support the support
     */
    public void setSupport(Collection<FHIRReference> support) {
        this.support = support;
    }

    /**
     * Returns the relatedPlan.
     * @return the relatedPlan
     */
    public Collection<FHIRCarePlanRelatedPlan> getRelatedPlan() {
        return relatedPlan;
    }

    /**
     * Sets the relatedPlan.
     * @param relatedPlan the relatedPlan
     */
    public void setRelatedPlan(Collection<FHIRCarePlanRelatedPlan> relatedPlan) {
        this.relatedPlan = relatedPlan;
    }

    /**
     * Returns the participant.
     * @return the participant
     */
    public Collection<FHIRCarePlanParticipant> getParticipant() {
        return participant;
    }

    /**
     * Sets the participant.
     * @param participant the participant
     */
    public void setParticipant(Collection<FHIRCarePlanParticipant> participant) {
        this.participant = participant;
    }

    /**
     * Returns the goal.
     * @return the goal
     */
    public Collection<FHIRReference> getGoal() {
        return goal;
    }

    /**
     * Sets the goal.
     * @param goal the goal
     */
    public void setGoal(Collection<FHIRReference> goal) {
        this.goal = goal;
    }

    /**
     * Returns the activity.
     * @return the activity
     */
    public Collection<FHIRCarePlanActivity> getActivity() {
        return activity;
    }

    /**
     * Sets the activity.
     * @param activity the activity
     */
    public void setActivity(Collection<FHIRCarePlanActivity> activity) {
        this.activity = activity;
    }

    /**
     * Returns the note.
     * @return the note
     */
    public FHIRAnnotation getNote() {
        return note;
    }

    /**
     * Sets the note.
     * @param note the note
     */
    public void setNote(FHIRAnnotation note) {
        this.note = note;
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
    private FHIRReference subject;
    private String status;
    private FHIRReference context;
    private FHIRPeriod period;
    private Collection<FHIRReference> author;
    private String modified;
    private Collection<FHIRCodeableConcept> category;
    private String description;
    private Collection<FHIRReference> addresses;
    private Collection<FHIRReference> support;
    private Collection<FHIRCarePlanRelatedPlan> relatedPlan;
    private Collection<FHIRCarePlanParticipant> participant;
    private Collection<FHIRReference> goal;
    private Collection<FHIRCarePlanActivity> activity;
    private FHIRAnnotation note;

}
