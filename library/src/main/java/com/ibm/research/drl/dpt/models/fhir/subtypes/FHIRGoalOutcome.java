
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGoalOutcome {

    private FHIRCodeableConcept resultCodeableConcept;
    private FHIRReference resultReference;

    public FHIRCodeableConcept getResultCodeableConcept() {
        return resultCodeableConcept;
    }

    public void setResultCodeableConcept(FHIRCodeableConcept resultCodeableConcept) {
        this.resultCodeableConcept = resultCodeableConcept;
    }

    public FHIRReference getResultReference() {
        return resultReference;
    }

    public void setResultReference(FHIRReference resultReference) {
        this.resultReference = resultReference;
    }
}


