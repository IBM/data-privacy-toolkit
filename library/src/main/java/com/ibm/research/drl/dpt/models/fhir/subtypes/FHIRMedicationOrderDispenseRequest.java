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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrderDispenseRequest {

    public FHIRCodeableConcept getMedicationCodeableConcept() {
        return medicationCodeableConcept;
    }

    public void setMedicationCodeableConcept(FHIRCodeableConcept medicationCodeableConcept) {
        this.medicationCodeableConcept = medicationCodeableConcept;
    }

    public FHIRReference getMedicationReference() {
        return medicationReference;
    }

    public void setMedicationReference(FHIRReference medicationReference) {
        this.medicationReference = medicationReference;
    }

    public FHIRPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(FHIRPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getNumberOfRepeatsAllowed() {
        return numberOfRepeatsAllowed;
    }

    public void setNumberOfRepeatsAllowed(String numberOfRepeatsAllowed) {
        this.numberOfRepeatsAllowed = numberOfRepeatsAllowed;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public FHIRQuantity getExpectedSupplyDuration() {
        return expectedSupplyDuration;
    }

    public void setExpectedSupplyDuration(FHIRQuantity expectedSupplyDuration) {
        this.expectedSupplyDuration = expectedSupplyDuration;
    }

    private FHIRCodeableConcept medicationCodeableConcept;
    private FHIRReference medicationReference;
    private FHIRPeriod validityPeriod;
    private String numberOfRepeatsAllowed;
    private FHIRQuantity quantity;
    private FHIRQuantity expectedSupplyDuration;

}


