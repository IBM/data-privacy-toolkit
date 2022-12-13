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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRatio;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationAdministrationDosage {
    private String text;
    private FHIRCodeableConcept siteCodeableConcept;
    private FHIRReference siteReference;
    private FHIRCodeableConcept route;
    private FHIRCodeableConcept method;
    private FHIRQuantity quantity;
    private FHIRRatio rateRatio;
    private FHIRRange rateRange;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FHIRCodeableConcept getSiteCodeableConcept() {
        return siteCodeableConcept;
    }

    public void setSiteCodeableConcept(FHIRCodeableConcept siteCodeableConcept) {
        this.siteCodeableConcept = siteCodeableConcept;
    }

    public FHIRReference getSiteReference() {
        return siteReference;
    }

    public void setSiteReference(FHIRReference siteReference) {
        this.siteReference = siteReference;
    }

    public FHIRCodeableConcept getRoute() {
        return route;
    }

    public void setRoute(FHIRCodeableConcept route) {
        this.route = route;
    }

    public FHIRCodeableConcept getMethod() {
        return method;
    }

    public void setMethod(FHIRCodeableConcept method) {
        this.method = method;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public FHIRRatio getRateRatio() {
        return rateRatio;
    }

    public void setRateRatio(FHIRRatio rateRatio) {
        this.rateRatio = rateRatio;
    }

    public FHIRRange getRateRange() {
        return rateRange;
    }

    public void setRateRange(FHIRRange rateRange) {
        this.rateRange = rateRange;
    }
}


