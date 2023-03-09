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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireGroup {

    private String title;
    private String linkId;
    private Collection<FHIRCoding> concept;
    private String text;
    private boolean required;
    private boolean repeats;
    private Collection<FHIRQuestionnaireGroup> group;
    private Collection<FHIRQuestionnaireGroupQuestion> question;

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

    public Collection<FHIRCoding> getConcept() {
        return concept;
    }

    public void setConcept(Collection<FHIRCoding> concept) {
        this.concept = concept;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRepeats() {
        return repeats;
    }

    public void setRepeats(boolean repeats) {
        this.repeats = repeats;
    }

    public Collection<FHIRQuestionnaireGroup> getGroup() {
        return group;
    }

    public void setGroup(Collection<FHIRQuestionnaireGroup> group) {
        this.group = group;
    }

    public Collection<FHIRQuestionnaireGroupQuestion> getQuestion() {
        return question;
    }

    public void setQuestion(Collection<FHIRQuestionnaireGroupQuestion> question) {
        this.question = question;
    }


}


