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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractTermValuedItem {

    private FHIRCodeableConcept entityCodeableConcept;
    private FHIRReference entityReference;
    private FHIRIdentifier identifier;
    private String effectiveTime;
    private FHIRQuantity quantity;
    private FHIRQuantity unitPrice;
    private float factor;
    private float points;
    private FHIRQuantity net;

    public FHIRCodeableConcept getEntityCodeableConcept() {
        return entityCodeableConcept;
    }

    public void setEntityCodeableConcept(FHIRCodeableConcept entityCodeableConcept) {
        this.entityCodeableConcept = entityCodeableConcept;
    }

    public FHIRReference getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(FHIRReference entityReference) {
        this.entityReference = entityReference;
    }

    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public FHIRQuantity getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(FHIRQuantity unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public FHIRQuantity getNet() {
        return net;
    }

    public void setNet(FHIRQuantity net) {
        this.net = net;
    }

}


