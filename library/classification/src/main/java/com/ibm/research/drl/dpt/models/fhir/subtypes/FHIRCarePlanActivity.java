/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAnnotation;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlanActivity {

    public Collection<FHIRReference> getActionResulting() {
        return actionResulting;
    }

    public void setActionResulting(Collection<FHIRReference> actionResulting) {
        this.actionResulting = actionResulting;
    }

    public Collection<FHIRAnnotation> getProgress() {
        return progress;
    }

    public void setProgress(Collection<FHIRAnnotation> progress) {
        this.progress = progress;
    }

    public FHIRReference getReference() {
        return reference;
    }

    public void setReference(FHIRReference reference) {
        this.reference = reference;
    }

    public FHIRCarePlanActivityDetail getDetail() {
        return detail;
    }

    public void setDetail(FHIRCarePlanActivityDetail detail) {
        this.detail = detail;
    }

    private Collection<FHIRReference> actionResulting;
    private Collection<FHIRAnnotation> progress;
    private FHIRReference reference;
    private FHIRCarePlanActivityDetail detail;

}


