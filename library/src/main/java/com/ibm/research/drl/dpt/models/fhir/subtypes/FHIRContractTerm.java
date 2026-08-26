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


/** FHIRContractTerm FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractTerm {
    /** Constructs a FHIRContractTerm. */
    public FHIRContractTerm() {}


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


    /**
     * Returns the identifier.
     * @return the identifier
     */
    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the issued.
     * @return the issued
     */
    public String getIssued() {
        return issued;
    }

    /**
     * Sets the issued.
     * @param issued the issued
     */
    public void setIssued(String issued) {
        this.issued = issued;
    }

    /**
     * Returns the applies.
     * @return the applies
     */
    public FHIRPeriod getApplies() {
        return applies;
    }

    /**
     * Sets the applies.
     * @param applies the applies
     */
    public void setApplies(FHIRPeriod applies) {
        this.applies = applies;
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
     * Returns the subType.
     * @return the subType
     */
    public FHIRCodeableConcept getSubType() {
        return subType;
    }

    /**
     * Sets the subType.
     * @param subType the subType
     */
    public void setSubType(FHIRCodeableConcept subType) {
        this.subType = subType;
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
     * Returns the action.
     * @return the action
     */
    public Collection<FHIRCodeableConcept> getAction() {
        return action;
    }

    /**
     * Sets the action.
     * @param action the action
     */
    public void setAction(Collection<FHIRCodeableConcept> action) {
        this.action = action;
    }

    /**
     * Returns the actionReason.
     * @return the actionReason
     */
    public Collection<FHIRCodeableConcept> getActionReason() {
        return actionReason;
    }

    /**
     * Sets the actionReason.
     * @param actionReason the actionReason
     */
    public void setActionReason(Collection<FHIRCodeableConcept> actionReason) {
        this.actionReason = actionReason;
    }

    /**
     * Returns the actor.
     * @return the actor
     */
    public Collection<FHIRContractTermActor> getActor() {
        return actor;
    }

    /**
     * Sets the actor.
     * @param actor the actor
     */
    public void setActor(Collection<FHIRContractTermActor> actor) {
        this.actor = actor;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the valuedItem.
     * @return the valuedItem
     */
    public Collection<FHIRContractTermValuedItem> getValuedItem() {
        return valuedItem;
    }

    /**
     * Sets the valuedItem.
     * @param valuedItem the valuedItem
     */
    public void setValuedItem(Collection<FHIRContractTermValuedItem> valuedItem) {
        this.valuedItem = valuedItem;
    }

    /**
     * Returns the group.
     * @return the group
     */
    public Collection<FHIRContractTerm> getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group the group
     */
    public void setGroup(Collection<FHIRContractTerm> group) {
        this.group = group;
    }

}


