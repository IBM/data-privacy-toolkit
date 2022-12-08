/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireGroupQuestion {

    private String linkId;
    private Collection<FHIRCoding> concept;
    private String text;
    private String type;
    private boolean required;
    private boolean repeats;
    private FHIRReference options;
    private Collection<FHIRCoding> option;
    private Collection<FHIRQuestionnaireGroup> group;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public FHIRReference getOptions() {
        return options;
    }

    public void setOptions(FHIRReference options) {
        this.options = options;
    }

    public Collection<FHIRCoding> getOption() {
        return option;
    }

    public void setOption(Collection<FHIRCoding> option) {
        this.option = option;
    }

    public Collection<FHIRQuestionnaireGroup> getGroup() {
        return group;
    }

    public void setGroup(Collection<FHIRQuestionnaireGroup> group) {
        this.group = group;
    }

}


