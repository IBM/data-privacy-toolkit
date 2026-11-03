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

import java.util.Collection;

/** FHIRQuestionnaireResponseGroup FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroup {
    /** Constructs a FHIRQuestionnaireResponseGroup. */
    public FHIRQuestionnaireResponseGroup() {}


    private String linkId;
    private String title;
    private String text;
    private FHIRReference subject;
    private Collection<FHIRQuestionnaireResponseGroup> group;
    private Collection<FHIRQuestionnaireResponseGroupQuestion> question;

    /**
     * Returns the linkId.
     * @return the linkId
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * Sets the linkId.
     * @param linkId the linkId
     */
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    /**
     * Returns the title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * Returns the group.
     * @return the group
     */
    public Collection<FHIRQuestionnaireResponseGroup> getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group the group
     */
    public void setGroup(Collection<FHIRQuestionnaireResponseGroup> group) {
        this.group = group;
    }

    /**
     * Returns the question.
     * @return the question
     */
    public Collection<FHIRQuestionnaireResponseGroupQuestion> getQuestion() {
        return question;
    }

    /**
     * Sets the question.
     * @param question the question
     */
    public void setQuestion(Collection<FHIRQuestionnaireResponseGroupQuestion> question) {
        this.question = question;
    }

}


