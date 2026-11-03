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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;

/** FHIRContractTermValuedItem FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractTermValuedItem {
    /** Constructs a FHIRContractTermValuedItem. */
    public FHIRContractTermValuedItem() {}


    private FHIRCodeableConcept entityCodeableConcept;
    private FHIRReference entityReference;
    private FHIRIdentifier identifier;
    private String effectiveTime;
    private FHIRQuantity quantity;
    private FHIRQuantity unitPrice;
    private float factor;
    private float points;
    private FHIRQuantity net;

    /**
     * Returns the entityCodeableConcept.
     * @return the entityCodeableConcept
     */
    public FHIRCodeableConcept getEntityCodeableConcept() {
        return entityCodeableConcept;
    }

    /**
     * Sets the entityCodeableConcept.
     * @param entityCodeableConcept the entityCodeableConcept
     */
    public void setEntityCodeableConcept(FHIRCodeableConcept entityCodeableConcept) {
        this.entityCodeableConcept = entityCodeableConcept;
    }

    /**
     * Returns the entityReference.
     * @return the entityReference
     */
    public FHIRReference getEntityReference() {
        return entityReference;
    }

    /**
     * Sets the entityReference.
     * @param entityReference the entityReference
     */
    public void setEntityReference(FHIRReference entityReference) {
        this.entityReference = entityReference;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the effectiveTime.
     * @return the effectiveTime
     */
    public String getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * Sets the effectiveTime.
     * @param effectiveTime the effectiveTime
     */
    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * Returns the quantity.
     * @return the quantity
     */
    public FHIRQuantity getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * @param quantity the quantity
     */
    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the unitPrice.
     * @return the unitPrice
     */
    public FHIRQuantity getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unitPrice.
     * @param unitPrice the unitPrice
     */
    public void setUnitPrice(FHIRQuantity unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Returns the factor.
     * @return the factor
     */
    public float getFactor() {
        return factor;
    }

    /**
     * Sets the factor.
     * @param factor the factor
     */
    public void setFactor(float factor) {
        this.factor = factor;
    }

    /**
     * Returns the points.
     * @return the points
     */
    public float getPoints() {
        return points;
    }

    /**
     * Sets the points.
     * @param points the points
     */
    public void setPoints(float points) {
        this.points = points;
    }

    /**
     * Returns the net.
     * @return the net
     */
    public FHIRQuantity getNet() {
        return net;
    }

    /**
     * Sets the net.
     * @param net the net
     */
    public void setNet(FHIRQuantity net) {
        this.net = net;
    }

}


