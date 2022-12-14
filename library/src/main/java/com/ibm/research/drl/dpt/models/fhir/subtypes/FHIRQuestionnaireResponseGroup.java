/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroup {

    private String linkId;
    private String title;
    private String text;
    private FHIRReference subject;
    private Collection<FHIRQuestionnaireResponseGroup> group;
    private Collection<FHIRQuestionnaireResponseGroupQuestion> question;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FHIRReference getSubject() {
        return subject;
    }

    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    public Collection<FHIRQuestionnaireResponseGroup> getGroup() {
        return group;
    }

    public void setGroup(Collection<FHIRQuestionnaireResponseGroup> group) {
        this.group = group;
    }

    public Collection<FHIRQuestionnaireResponseGroupQuestion> getQuestion() {
        return question;
    }

    public void setQuestion(Collection<FHIRQuestionnaireResponseGroupQuestion> question) {
        this.question = question;
    }

}


