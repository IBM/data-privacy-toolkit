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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRQuestionnaireResponseGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponse extends FHIRBaseDomainResource {

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String resourceType;
    private FHIRIdentifier identifier;
    private FHIRReference questionnaire;
    private String status;
    private FHIRReference subject;
    private FHIRReference author;
    private String authored;
    private FHIRReference source;
    private FHIRReference encounter;
    private FHIRQuestionnaireResponseGroup group;

    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    public FHIRReference getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(FHIRReference questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FHIRReference getSubject() {
        return subject;
    }

    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    public FHIRReference getAuthor() {
        return author;
    }

    public void setAuthor(FHIRReference author) {
        this.author = author;
    }

    public String getAuthored() {
        return authored;
    }

    public void setAuthored(String authored) {
        this.authored = authored;
    }

    public FHIRReference getSource() {
        return source;
    }

    public void setSource(FHIRReference source) {
        this.source = source;
    }

    public FHIRReference getEncounter() {
        return encounter;
    }

    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    public FHIRQuestionnaireResponseGroup getGroup() {
        return group;
    }

    public void setGroup(FHIRQuestionnaireResponseGroup group) {
        this.group = group;
    }

}
