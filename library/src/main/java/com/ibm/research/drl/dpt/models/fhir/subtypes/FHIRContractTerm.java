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
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractTerm {

    private FHIRIdentifier identifier;
    private String issued;
    private FHIRPeriod applies;
    private FHIRCodeableConcept type;
    private FHIRCodeableConcept subType;
    private FHIRReference subject;
    private Collection<FHIRCodeableConcept> action;
    private Collection<FHIRCodeableConcept> actionReason;
    private Collection<FHIRContractTermActor> actor;
    private String text;
    private Collection<FHIRContractTermValuedItem> valuedItem;
    private Collection<FHIRContractTerm> group;


    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public FHIRPeriod getApplies() {
        return applies;
    }

    public void setApplies(FHIRPeriod applies) {
        this.applies = applies;
    }

    public FHIRCodeableConcept getType() {
        return type;
    }

    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    public FHIRCodeableConcept getSubType() {
        return subType;
    }

    public void setSubType(FHIRCodeableConcept subType) {
        this.subType = subType;
    }

    public FHIRReference getSubject() {
        return subject;
    }

    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    public Collection<FHIRCodeableConcept> getAction() {
        return action;
    }

    public void setAction(Collection<FHIRCodeableConcept> action) {
        this.action = action;
    }

    public Collection<FHIRCodeableConcept> getActionReason() {
        return actionReason;
    }

    public void setActionReason(Collection<FHIRCodeableConcept> actionReason) {
        this.actionReason = actionReason;
    }

    public Collection<FHIRContractTermActor> getActor() {
        return actor;
    }

    public void setActor(Collection<FHIRContractTermActor> actor) {
        this.actor = actor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<FHIRContractTermValuedItem> getValuedItem() {
        return valuedItem;
    }

    public void setValuedItem(Collection<FHIRContractTermValuedItem> valuedItem) {
        this.valuedItem = valuedItem;
    }

    public Collection<FHIRContractTerm> getGroup() {
        return group;
    }

    public void setGroup(Collection<FHIRContractTerm> group) {
        this.group = group;
    }

}


