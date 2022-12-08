/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlanParticipant {

    private FHIRCodeableConcept role;
    private FHIRReference member;

    public FHIRReference getMember() {
        return member;
    }

    public void setMember(FHIRReference member) {
        this.member = member;
    }

    public FHIRCodeableConcept getRole() {
        return role;
    }

    public void setRole(FHIRCodeableConcept role) {
        this.role = role;
    }

}


