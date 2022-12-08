/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractRule {
    private FHIRAttachment contentAttachment;
    private FHIRReference contentReference;

    public FHIRReference getContentReference() {
        return contentReference;
    }

    public void setContentReference(FHIRReference contentReference) {
        this.contentReference = contentReference;
    }

    public FHIRAttachment getContentAttachment() {
        return contentAttachment;
    }

    public void setContentAttachment(FHIRAttachment contentAttachment) {
        this.contentAttachment = contentAttachment;
    }

}


