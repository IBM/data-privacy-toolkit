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
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRObservationComponent;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRObservationReferenceRange;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRObservationRelated;

import java.util.Collection;


/** FHIRObservation FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRObservation extends FHIRBaseDomainResource {
    /** Constructs a FHIRObservation. */
    public FHIRObservation() {}



    /**
     * Returns the category.
     * @return the category
     */
    public FHIRCodeableConcept getCategory() {
        return category;
    }

    /**
     * Sets the category.
     * @param category the category
     */
    public void setCategory(FHIRCodeableConcept category) {
        this.category = category;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

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
     * Returns the subject.
     * @return the subject
     */
    public FHIRReference getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     * @param subject the subject
     */
    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    /**
     * Returns the encounter.
     * @return the encounter
     */
    public FHIRReference getEncounter() {
        return encounter;
    }

    /**
     * Sets the encounter.
     * @param encounter the encounter
     */
    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    /**
     * Returns the effectiveDateTime.
     * @return the effectiveDateTime
     */
    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    /**
     * Sets the effectiveDateTime.
     * @param effectiveDateTime the effectiveDateTime
     */
    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    /**
     * Returns the effectivePeriod.
     * @return the effectivePeriod
     */
    public FHIRPeriod getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Sets the effectivePeriod.
     * @param effectivePeriod the effectivePeriod
     */
    public void setEffectivePeriod(FHIRPeriod effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    /**
     * Returns the issued.
     * @return the issued
     */
    public String getIssued() {
        return issued;
    }

    /**
     * Sets the issued.
     * @param issued the issued
     */
    public void setIssued(String issued) {
        this.issued = issued;
    }

    /**
     * Returns the performer.
     * @return the performer
     */
    public Collection<FHIRReference> getPerformer() {
        return performer;
    }

    /**
     * Sets the performer.
     * @param performer the performer
     */
    public void setPerformer(Collection<FHIRReference> performer) {
        this.performer = performer;
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
     * Returns the interpretation.
     * @return the interpretation
     */
    public FHIRCodeableConcept getInterpretation() {
        return interpretation;
    }

    /**
     * Sets the interpretation.
     * @param interpretation the interpretation
     */
    public void setInterpretation(FHIRCodeableConcept interpretation) {
        this.interpretation = interpretation;
    }

    /**
     * Returns the comments.
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments.
     * @param comments the comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns the bodySite.
     * @return the bodySite
     */
    public FHIRCodeableConcept getBodySite() {
        return bodySite;
    }

    /**
     * Sets the bodySite.
     * @param bodySite the bodySite
     */
    public void setBodySite(FHIRCodeableConcept bodySite) {
        this.bodySite = bodySite;
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
     * Returns the specimen.
     * @return the specimen
     */
    public FHIRReference getSpecimen() {
        return specimen;
    }

    /**
     * Sets the specimen.
     * @param specimen the specimen
     */
    public void setSpecimen(FHIRReference specimen) {
        this.specimen = specimen;
    }

    /**
     * Returns the device.
     * @return the device
     */
    public FHIRReference getDevice() {
        return device;
    }

    /**
     * Sets the device.
     * @param device the device
     */
    public void setDevice(FHIRReference device) {
        this.device = device;
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

    /**
     * Returns the related.
     * @return the related
     */
    public Collection<FHIRObservationRelated> getRelated() {
        return related;
    }

    /**
     * Sets the related.
     * @param related the related
     */
    public void setRelated(Collection<FHIRObservationRelated> related) {
        this.related = related;
    }

    /**
     * Returns the component.
     * @return the component
     */
    public Collection<FHIRObservationComponent> getComponent() {
        return component;
    }

    /**
     * Sets the component.
     * @param component the component
     */
    public void setComponent(Collection<FHIRObservationComponent> component) {
        this.component = component;
    }

    /**
     * Returns the resourceType.
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resourceType.
     * @param resourceType the resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String resourceType;
    private Collection<FHIRIdentifier> identifier;
    private String status;
    private FHIRCodeableConcept category;
    private FHIRCodeableConcept code;
    private FHIRReference subject;
    private FHIRReference encounter;
    private String effectiveDateTime;
    private FHIRPeriod effectivePeriod;
    private String issued;
    private Collection<FHIRReference> performer;
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
    private FHIRCodeableConcept interpretation;
    private String comments;
    private FHIRCodeableConcept bodySite;
    private FHIRCodeableConcept method;
    private FHIRReference specimen;
    private FHIRReference device;
    private Collection<FHIRObservationReferenceRange> referenceRange;
    private Collection<FHIRObservationRelated> related;
    private Collection<FHIRObservationComponent> component;

}


