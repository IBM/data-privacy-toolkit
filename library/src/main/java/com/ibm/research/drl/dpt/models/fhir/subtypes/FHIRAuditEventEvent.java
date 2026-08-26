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

/** FHIRAuditEventEvent FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventEvent {
    /** Constructs a FHIRAuditEventEvent. */
    public FHIRAuditEventEvent() {}


    private FHIRCoding type;
    private Collection<FHIRCoding> subtype;
    private String action;
    private String dateTime;
    private String outcome;
    private String outcomeDesc;
    private Collection<FHIRCoding> purposeOfEvent;

    /**
     * Returns the type.
     * @return the type
     */
    public FHIRCoding getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(FHIRCoding type) {
        this.type = type;
    }

    /**
     * Returns the subtype.
     * @return the subtype
     */
    public Collection<FHIRCoding> getSubtype() {
        return subtype;
    }

    /**
     * Sets the subtype.
     * @param subtype the subtype
     */
    public void setSubtype(Collection<FHIRCoding> subtype) {
        this.subtype = subtype;
    }

    /**
     * Returns the action.
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action.
     * @param action the action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the dateTime.
     * @return the dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Sets the dateTime.
     * @param dateTime the dateTime
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the outcome.
     * @return the outcome
     */
    public String getOutcome() {
        return outcome;
    }

    /**
     * Sets the outcome.
     * @param outcome the outcome
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    /**
     * Returns the outcomeDesc.
     * @return the outcomeDesc
     */
    public String getOutcomeDesc() {
        return outcomeDesc;
    }

    /**
     * Sets the outcomeDesc.
     * @param outcomeDesc the outcomeDesc
     */
    public void setOutcomeDesc(String outcomeDesc) {
        this.outcomeDesc = outcomeDesc;
    }

    /**
     * Returns the purposeOfEvent.
     * @return the purposeOfEvent
     */
    public Collection<FHIRCoding> getPurposeOfEvent() {
        return purposeOfEvent;
    }

    /**
     * Sets the purposeOfEvent.
     * @param purposeOfEvent the purposeOfEvent
     */
    public void setPurposeOfEvent(Collection<FHIRCoding> purposeOfEvent) {
        this.purposeOfEvent = purposeOfEvent;
    }
}
