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

import java.util.Collection;

/** FHIRQuestionnaireResponseGroupQuestion FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroupQuestion {
    /** Constructs a FHIRQuestionnaireResponseGroupQuestion. */
    public FHIRQuestionnaireResponseGroupQuestion() {}


    private String linkId;
    private String text;
    private Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> answer;

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
     * Returns the answer.
     * @return the answer
     */
    public Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> getAnswer() {
        return answer;
    }

    /**
     * Sets the answer.
     * @param answer the answer
     */
    public void setAnswer(Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> answer) {
        this.answer = answer;
    }


}


