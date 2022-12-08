/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventEvent {

    private FHIRCoding type;
    private Collection<FHIRCoding> subtype;
    private String action;
    private String dateTime;
    private String outcome;
    private String outcomeDesc;
    private Collection<FHIRCoding> purposeOfEvent;

    public FHIRCoding getType() {
        return type;
    }

    public void setType(FHIRCoding type) {
        this.type = type;
    }

    public Collection<FHIRCoding> getSubtype() {
        return subtype;
    }

    public void setSubtype(Collection<FHIRCoding> subtype) {
        this.subtype = subtype;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcomeDesc() {
        return outcomeDesc;
    }

    public void setOutcomeDesc(String outcomeDesc) {
        this.outcomeDesc = outcomeDesc;
    }

    public Collection<FHIRCoding> getPurposeOfEvent() {
        return purposeOfEvent;
    }

    public void setPurposeOfEvent(Collection<FHIRCoding> purposeOfEvent) {
        this.purposeOfEvent = purposeOfEvent;
    }
}
