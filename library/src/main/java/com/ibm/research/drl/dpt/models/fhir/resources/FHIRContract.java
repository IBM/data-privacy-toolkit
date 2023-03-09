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


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContract extends FHIRBaseDomainResource {

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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


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

    public Collection<FHIRReference> getSubject() {
        return subject;
    }

    public void setSubject(Collection<FHIRReference> subject) {
        this.subject = subject;
    }

    public Collection<FHIRReference> getAuthority() {
        return authority;
    }

    public void setAuthority(Collection<FHIRReference> authority) {
        this.authority = authority;
    }

    public Collection<FHIRReference> getDomain() {
        return domain;
    }

    public void setDomain(Collection<FHIRReference> domain) {
        this.domain = domain;
    }

    public FHIRCodeableConcept getType() {
        return type;
    }

    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    public Collection<FHIRCodeableConcept> getSubType() {
        return subType;
    }

    public void setSubType(Collection<FHIRCodeableConcept> subType) {
        this.subType = subType;
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

    public Collection<FHIRContractActor> getActor() {
        return actor;
    }

    public void setActor(Collection<FHIRContractActor> actor) {
        this.actor = actor;
    }

    public Collection<FHIRContractValuedItem> getValuedItem() {
        return valuedItem;
    }

    public void setValuedItem(Collection<FHIRContractValuedItem> valuedItem) {
        this.valuedItem = valuedItem;
    }

    public Collection<FHIRContractSigner> getSigner() {
        return signer;
    }

    public void setSigner(Collection<FHIRContractSigner> signer) {
        this.signer = signer;
    }

    public Collection<FHIRContractTerm> getTerm() {
        return term;
    }

    public void setTerm(Collection<FHIRContractTerm> term) {
        this.term = term;
    }

    public FHIRAttachment getBindingAttachment() {
        return bindingAttachment;
    }

    public void setBindingAttachment(FHIRAttachment bindingAttachment) {
        this.bindingAttachment = bindingAttachment;
    }

    public FHIRReference getBindingReference() {
        return bindingReference;
    }

    public void setBindingReference(FHIRReference bindingReference) {
        this.bindingReference = bindingReference;
    }

    public Collection<FHIRContractFriendly> getFriendly() {
        return friendly;
    }

    public void setFriendly(Collection<FHIRContractFriendly> friendly) {
        this.friendly = friendly;
    }

    public Collection<FHIRContractLegal> getLegal() {
        return legal;
    }

    public void setLegal(Collection<FHIRContractLegal> legal) {
        this.legal = legal;
    }

    public Collection<FHIRContractRule> getRule() {
        return rule;
    }

    public void setRule(Collection<FHIRContractRule> rule) {
        this.rule = rule;
    }

}
