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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRQuestionnaireResponseGroup;

/** FHIRQuestionnaireResponse FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponse extends FHIRBaseDomainResource {
    /** Constructs a FHIRQuestionnaireResponse. */
    public FHIRQuestionnaireResponse() {}


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
    private FHIRIdentifier identifier;
    private FHIRReference questionnaire;
    private String status;
    private FHIRReference subject;
    private FHIRReference author;
    private String authored;
    private FHIRReference source;
    private FHIRReference encounter;
    private FHIRQuestionnaireResponseGroup group;

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
     * Returns the questionnaire.
     * @return the questionnaire
     */
    public FHIRReference getQuestionnaire() {
        return questionnaire;
    }

    /**
     * Sets the questionnaire.
     * @param questionnaire the questionnaire
     */
    public void setQuestionnaire(FHIRReference questionnaire) {
        this.questionnaire = questionnaire;
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
     * Returns the authored.
     * @return the authored
     */
    public String getAuthored() {
        return authored;
    }

    /**
     * Sets the authored.
     * @param authored the authored
     */
    public void setAuthored(String authored) {
        this.authored = authored;
    }

    /**
     * Returns the source.
     * @return the source
     */
    public FHIRReference getSource() {
        return source;
    }

    /**
     * Sets the source.
     * @param source the source
     */
    public void setSource(FHIRReference source) {
        this.source = source;
    }

    /**
     * Returns the encounter.
     * @return the encounter
     */
    public FHIRReference getEncounter() {
        return encounter;
    }

    /**
     * Sets the encounter.
     * @param encounter the encounter
     */
    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    /**
     * Returns the group.
     * @return the group
     */
    public FHIRQuestionnaireResponseGroup getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group the group
     */
    public void setGroup(FHIRQuestionnaireResponseGroup group) {
        this.group = group;
    }

}
