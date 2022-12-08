/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroupQuestion {

    private String linkId;
    private String text;
    private Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> answer;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> getAnswer() {
        return answer;
    }

    public void setAnswer(Collection<FHIRQuestionnaireResponseGroupQuestionAnswer> answer) {
        this.answer = answer;
    }


}


