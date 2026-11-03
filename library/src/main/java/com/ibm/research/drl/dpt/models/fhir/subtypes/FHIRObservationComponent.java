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
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;

import java.util.Collection;


/** FHIRObservationComponent FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRObservationComponent {
    /** Constructs a FHIRObservationComponent. */
    public FHIRObservationComponent() {}


    private FHIRCodeableConcept code;
    private FHIRQuantity valueQuantity;
    private FHIRCodeableConcept valueCodeableConcept;
    private String valueString;
    private FHIRRange valueRange;
    private FHIRRatio valueRatio;
    private FHIRSampledData valueSampledData;
    private FHIRAttachment valueAttachment;
    private String valueTime;
    private String valueDateTime;
    private FHIRPeriod valuePeriod;
    private FHIRCodeableConcept dataAbsentReason;
    private Collection<FHIRObservationReferenceRange> referenceRange;

    /**
     * Returns the code.
     * @return the code
     */
    public FHIRCodeableConcept getCode() {
        return code;
    }

    /**
     * Sets the code.
     * @param code the code
     */
    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    /**
     * Returns the valueQuantity.
     * @return the valueQuantity
     */
    public FHIRQuantity getValueQuantity() {
        return valueQuantity;
    }

    /**
     * Sets the valueQuantity.
     * @param valueQuantity the valueQuantity
     */
    public void setValueQuantity(FHIRQuantity valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    /**
     * Returns the valueCodeableConcept.
     * @return the valueCodeableConcept
     */
    public FHIRCodeableConcept getValueCodeableConcept() {
        return valueCodeableConcept;
    }

    /**
     * Sets the valueCodeableConcept.
     * @param valueCodeableConcept the valueCodeableConcept
     */
    public void setValueCodeableConcept(FHIRCodeableConcept valueCodeableConcept) {
        this.valueCodeableConcept = valueCodeableConcept;
    }

    /**
     * Returns the valueString.
     * @return the valueString
     */
    public String getValueString() {
        return valueString;
    }

    /**
     * Sets the valueString.
     * @param valueString the valueString
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * Returns the valueRange.
     * @return the valueRange
     */
    public FHIRRange getValueRange() {
        return valueRange;
    }

    /**
     * Sets the valueRange.
     * @param valueRange the valueRange
     */
    public void setValueRange(FHIRRange valueRange) {
        this.valueRange = valueRange;
    }

    /**
     * Returns the valueRatio.
     * @return the valueRatio
     */
    public FHIRRatio getValueRatio() {
        return valueRatio;
    }

    /**
     * Sets the valueRatio.
     * @param valueRatio the valueRatio
     */
    public void setValueRatio(FHIRRatio valueRatio) {
        this.valueRatio = valueRatio;
    }

    /**
     * Returns the valueSampledData.
     * @return the valueSampledData
     */
    public FHIRSampledData getValueSampledData() {
        return valueSampledData;
    }

    /**
     * Sets the valueSampledData.
     * @param valueSampledData the valueSampledData
     */
    public void setValueSampledData(FHIRSampledData valueSampledData) {
        this.valueSampledData = valueSampledData;
    }

    /**
     * Returns the valueAttachment.
     * @return the valueAttachment
     */
    public FHIRAttachment getValueAttachment() {
        return valueAttachment;
    }

    /**
     * Sets the valueAttachment.
     * @param valueAttachment the valueAttachment
     */
    public void setValueAttachment(FHIRAttachment valueAttachment) {
        this.valueAttachment = valueAttachment;
    }

    /**
     * Returns the valueTime.
     * @return the valueTime
     */
    public String getValueTime() {
        return valueTime;
    }

    /**
     * Sets the valueTime.
     * @param valueTime the valueTime
     */
    public void setValueTime(String valueTime) {
        this.valueTime = valueTime;
    }

    /**
     * Returns the valueDateTime.
     * @return the valueDateTime
     */
    public String getValueDateTime() {
        return valueDateTime;
    }

    /**
     * Sets the valueDateTime.
     * @param valueDateTime the valueDateTime
     */
    public void setValueDateTime(String valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    /**
     * Returns the valuePeriod.
     * @return the valuePeriod
     */
    public FHIRPeriod getValuePeriod() {
        return valuePeriod;
    }

    /**
     * Sets the valuePeriod.
     * @param valuePeriod the valuePeriod
     */
    public void setValuePeriod(FHIRPeriod valuePeriod) {
        this.valuePeriod = valuePeriod;
    }

    /**
     * Returns the dataAbsentReason.
     * @return the dataAbsentReason
     */
    public FHIRCodeableConcept getDataAbsentReason() {
        return dataAbsentReason;
    }

    /**
     * Sets the dataAbsentReason.
     * @param dataAbsentReason the dataAbsentReason
     */
    public void setDataAbsentReason(FHIRCodeableConcept dataAbsentReason) {
        this.dataAbsentReason = dataAbsentReason;
    }

    /**
     * Returns the referenceRange.
     * @return the referenceRange
     */
    public Collection<FHIRObservationReferenceRange> getReferenceRange() {
        return referenceRange;
    }

    /**
     * Sets the referenceRange.
     * @param referenceRange the referenceRange
     */
    public void setReferenceRange(Collection<FHIRObservationReferenceRange> referenceRange) {
        this.referenceRange = referenceRange;
    }

}


