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
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;

/** FHIRMedicationOrderDosageInstruction FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrderDosageInstruction {
    /** Constructs a FHIRMedicationOrderDosageInstruction. */
    public FHIRMedicationOrderDosageInstruction() {}


    private String text;
    private FHIRCodeableConcept additionalInstructions;
    private FHIRTiming timing;
    private boolean asNeededBoolean;
    private FHIRCodeableConcept asNeededCodeableConcept;
    private FHIRCodeableConcept siteCodeableConcept;
    private FHIRReference siteReference;
    private FHIRCodeableConcept route;
    private FHIRCodeableConcept method;
    private FHIRRange doseRange;
    private FHIRQuantity doseQuantity;
    private FHIRRatio rateRatio;
    private FHIRRange rateRange;
    private FHIRRatio maxDosePerPeriod;

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the additionalInstructions.
     * @return the additionalInstructions
     */
    public FHIRCodeableConcept getAdditionalInstructions() {
        return additionalInstructions;
    }

    /**
     * Sets the additionalInstructions.
     * @param additionalInstructions the additionalInstructions
     */
    public void setAdditionalInstructions(FHIRCodeableConcept additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    /**
     * Returns the timing.
     * @return the timing
     */
    public FHIRTiming getTiming() {
        return timing;
    }

    /**
     * Sets the timing.
     * @param timing the timing
     */
    public void setTiming(FHIRTiming timing) {
        this.timing = timing;
    }

    /**
     * Returns the asNeededBoolean.
     * @return the asNeededBoolean
     */
    public boolean isAsNeededBoolean() {
        return asNeededBoolean;
    }

    /**
     * Sets the asNeededBoolean.
     * @param asNeededBoolean the asNeededBoolean
     */
    public void setAsNeededBoolean(boolean asNeededBoolean) {
        this.asNeededBoolean = asNeededBoolean;
    }

    /**
     * Returns the asNeededCodeableConcept.
     * @return the asNeededCodeableConcept
     */
    public FHIRCodeableConcept getAsNeededCodeableConcept() {
        return asNeededCodeableConcept;
    }

    /**
     * Sets the asNeededCodeableConcept.
     * @param asNeededCodeableConcept the asNeededCodeableConcept
     */
    public void setAsNeededCodeableConcept(FHIRCodeableConcept asNeededCodeableConcept) {
        this.asNeededCodeableConcept = asNeededCodeableConcept;
    }

    /**
     * Returns the siteCodeableConcept.
     * @return the siteCodeableConcept
     */
    public FHIRCodeableConcept getSiteCodeableConcept() {
        return siteCodeableConcept;
    }

    /**
     * Sets the siteCodeableConcept.
     * @param siteCodeableConcept the siteCodeableConcept
     */
    public void setSiteCodeableConcept(FHIRCodeableConcept siteCodeableConcept) {
        this.siteCodeableConcept = siteCodeableConcept;
    }

    /**
     * Returns the siteReference.
     * @return the siteReference
     */
    public FHIRReference getSiteReference() {
        return siteReference;
    }

    /**
     * Sets the siteReference.
     * @param siteReference the siteReference
     */
    public void setSiteReference(FHIRReference siteReference) {
        this.siteReference = siteReference;
    }

    /**
     * Returns the route.
     * @return the route
     */
    public FHIRCodeableConcept getRoute() {
        return route;
    }

    /**
     * Sets the route.
     * @param route the route
     */
    public void setRoute(FHIRCodeableConcept route) {
        this.route = route;
    }

    /**
     * Returns the method.
     * @return the method
     */
    public FHIRCodeableConcept getMethod() {
        return method;
    }

    /**
     * Sets the method.
     * @param method the method
     */
    public void setMethod(FHIRCodeableConcept method) {
        this.method = method;
    }

    /**
     * Returns the doseRange.
     * @return the doseRange
     */
    public FHIRRange getDoseRange() {
        return doseRange;
    }

    /**
     * Sets the doseRange.
     * @param doseRange the doseRange
     */
    public void setDoseRange(FHIRRange doseRange) {
        this.doseRange = doseRange;
    }

    /**
     * Returns the doseQuantity.
     * @return the doseQuantity
     */
    public FHIRQuantity getDoseQuantity() {
        return doseQuantity;
    }

    /**
     * Sets the doseQuantity.
     * @param doseQuantity the doseQuantity
     */
    public void setDoseQuantity(FHIRQuantity doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    /**
     * Returns the rateRatio.
     * @return the rateRatio
     */
    public FHIRRatio getRateRatio() {
        return rateRatio;
    }

    /**
     * Sets the rateRatio.
     * @param rateRatio the rateRatio
     */
    public void setRateRatio(FHIRRatio rateRatio) {
        this.rateRatio = rateRatio;
    }

    /**
     * Returns the rateRange.
     * @return the rateRange
     */
    public FHIRRange getRateRange() {
        return rateRange;
    }

    /**
     * Sets the rateRange.
     * @param rateRange the rateRange
     */
    public void setRateRange(FHIRRange rateRange) {
        this.rateRange = rateRange;
    }

    /**
     * Returns the maxDosePerPeriod.
     * @return the maxDosePerPeriod
     */
    public FHIRRatio getMaxDosePerPeriod() {
        return maxDosePerPeriod;
    }

    /**
     * Sets the maxDosePerPeriod.
     * @param maxDosePerPeriod the maxDosePerPeriod
     */
    public void setMaxDosePerPeriod(FHIRRatio maxDosePerPeriod) {
        this.maxDosePerPeriod = maxDosePerPeriod;
    }

}
