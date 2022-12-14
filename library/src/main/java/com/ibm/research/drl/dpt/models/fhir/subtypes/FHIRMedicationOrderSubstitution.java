/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrderSubstitution {
    public FHIRCodeableConcept getType() {
        return type;
    }

    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    public FHIRCodeableConcept getReason() {
        return reason;
    }

    public void setReason(FHIRCodeableConcept reason) {
        this.reason = reason;
    }

    private FHIRCodeableConcept type;
    private FHIRCodeableConcept reason;
}


