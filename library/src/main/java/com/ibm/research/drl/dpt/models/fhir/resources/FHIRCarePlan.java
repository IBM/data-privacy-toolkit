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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlan extends FHIRBaseDomainResource {

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FHIRReference getContext() {
        return context;
    }

    public void setContext(FHIRReference context) {
        this.context = context;
    }

    public FHIRPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    public Collection<FHIRReference> getAuthor() {
        return author;
    }

    public void setAuthor(Collection<FHIRReference> author) {
        this.author = author;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
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

    public Collection<FHIRReference> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<FHIRReference> addresses) {
        this.addresses = addresses;
    }

    public Collection<FHIRReference> getSupport() {
        return support;
    }

    public void setSupport(Collection<FHIRReference> support) {
        this.support = support;
    }

    public Collection<FHIRCarePlanRelatedPlan> getRelatedPlan() {
        return relatedPlan;
    }

    public void setRelatedPlan(Collection<FHIRCarePlanRelatedPlan> relatedPlan) {
        this.relatedPlan = relatedPlan;
    }

    public Collection<FHIRCarePlanParticipant> getParticipant() {
        return participant;
    }

    public void setParticipant(Collection<FHIRCarePlanParticipant> participant) {
        this.participant = participant;
    }

    public Collection<FHIRReference> getGoal() {
        return goal;
    }

    public void setGoal(Collection<FHIRReference> goal) {
        this.goal = goal;
    }

    public Collection<FHIRCarePlanActivity> getActivity() {
        return activity;
    }

    public void setActivity(Collection<FHIRCarePlanActivity> activity) {
        this.activity = activity;
    }

    public FHIRAnnotation getNote() {
        return note;
    }

    public void setNote(FHIRAnnotation note) {
        this.note = note;
    }

    public String getResourceType() {
        return resourceType;
    }

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
