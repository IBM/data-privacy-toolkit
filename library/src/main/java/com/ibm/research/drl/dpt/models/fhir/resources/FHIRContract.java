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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.subtypes.*;

import java.util.Collection;


/** FHIRContract FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContract extends FHIRBaseDomainResource {
    /** Constructs a FHIRContract. */
    public FHIRContract() {}


    private FHIRIdentifier identifier;
    private String issued;
    private FHIRPeriod applies;
    private Collection<FHIRReference> subject;
    private Collection<FHIRReference> authority;
    private Collection<FHIRReference> domain;
    private FHIRCodeableConcept type;
    private Collection<FHIRCodeableConcept> subType;
    private Collection<FHIRCodeableConcept> action;
    private Collection<FHIRCodeableConcept> actionReason;
    private Collection<FHIRContractActor> actor;
    private Collection<FHIRContractValuedItem> valuedItem;
    private Collection<FHIRContractSigner> signer;
    private Collection<FHIRContractTerm> term;
    private FHIRAttachment bindingAttachment;
    private FHIRReference bindingReference;
    private Collection<FHIRContractFriendly> friendly;
    private Collection<FHIRContractLegal> legal;
    private Collection<FHIRContractRule> rule;
    private String resourceType;

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
     * Returns the subject.
     * @return the subject
     */
    public Collection<FHIRReference> getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     * @param subject the subject
     */
    public void setSubject(Collection<FHIRReference> subject) {
        this.subject = subject;
    }

    /**
     * Returns the authority.
     * @return the authority
     */
    public Collection<FHIRReference> getAuthority() {
        return authority;
    }

    /**
     * Sets the authority.
     * @param authority the authority
     */
    public void setAuthority(Collection<FHIRReference> authority) {
        this.authority = authority;
    }

    /**
     * Returns the domain.
     * @return the domain
     */
    public Collection<FHIRReference> getDomain() {
        return domain;
    }

    /**
     * Sets the domain.
     * @param domain the domain
     */
    public void setDomain(Collection<FHIRReference> domain) {
        this.domain = domain;
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
    public Collection<FHIRCodeableConcept> getSubType() {
        return subType;
    }

    /**
     * Sets the subType.
     * @param subType the subType
     */
    public void setSubType(Collection<FHIRCodeableConcept> subType) {
        this.subType = subType;
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
    public Collection<FHIRContractActor> getActor() {
        return actor;
    }

    /**
     * Sets the actor.
     * @param actor the actor
     */
    public void setActor(Collection<FHIRContractActor> actor) {
        this.actor = actor;
    }

    /**
     * Returns the valuedItem.
     * @return the valuedItem
     */
    public Collection<FHIRContractValuedItem> getValuedItem() {
        return valuedItem;
    }

    /**
     * Sets the valuedItem.
     * @param valuedItem the valuedItem
     */
    public void setValuedItem(Collection<FHIRContractValuedItem> valuedItem) {
        this.valuedItem = valuedItem;
    }

    /**
     * Returns the signer.
     * @return the signer
     */
    public Collection<FHIRContractSigner> getSigner() {
        return signer;
    }

    /**
     * Sets the signer.
     * @param signer the signer
     */
    public void setSigner(Collection<FHIRContractSigner> signer) {
        this.signer = signer;
    }

    /**
     * Returns the term.
     * @return the term
     */
    public Collection<FHIRContractTerm> getTerm() {
        return term;
    }

    /**
     * Sets the term.
     * @param term the term
     */
    public void setTerm(Collection<FHIRContractTerm> term) {
        this.term = term;
    }

    /**
     * Returns the bindingAttachment.
     * @return the bindingAttachment
     */
    public FHIRAttachment getBindingAttachment() {
        return bindingAttachment;
    }

    /**
     * Sets the bindingAttachment.
     * @param bindingAttachment the bindingAttachment
     */
    public void setBindingAttachment(FHIRAttachment bindingAttachment) {
        this.bindingAttachment = bindingAttachment;
    }

    /**
     * Returns the bindingReference.
     * @return the bindingReference
     */
    public FHIRReference getBindingReference() {
        return bindingReference;
    }

    /**
     * Sets the bindingReference.
     * @param bindingReference the bindingReference
     */
    public void setBindingReference(FHIRReference bindingReference) {
        this.bindingReference = bindingReference;
    }

    /**
     * Returns the friendly.
     * @return the friendly
     */
    public Collection<FHIRContractFriendly> getFriendly() {
        return friendly;
    }

    /**
     * Sets the friendly.
     * @param friendly the friendly
     */
    public void setFriendly(Collection<FHIRContractFriendly> friendly) {
        this.friendly = friendly;
    }

    /**
     * Returns the legal.
     * @return the legal
     */
    public Collection<FHIRContractLegal> getLegal() {
        return legal;
    }

    /**
     * Sets the legal.
     * @param legal the legal
     */
    public void setLegal(Collection<FHIRContractLegal> legal) {
        this.legal = legal;
    }

    /**
     * Returns the rule.
     * @return the rule
     */
    public Collection<FHIRContractRule> getRule() {
        return rule;
    }

    /**
     * Sets the rule.
     * @param rule the rule
     */
    public void setRule(Collection<FHIRContractRule> rule) {
        this.rule = rule;
    }

}
