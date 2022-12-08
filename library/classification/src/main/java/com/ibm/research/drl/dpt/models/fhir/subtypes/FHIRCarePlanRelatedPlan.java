/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlanRelatedPlan {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FHIRReference getPlan() {
        return plan;
    }

    public void setPlan(FHIRReference plan) {
        this.plan = plan;
    }

    private String code;
    private FHIRReference plan;
}


