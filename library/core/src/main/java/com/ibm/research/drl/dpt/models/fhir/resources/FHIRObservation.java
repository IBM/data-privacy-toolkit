/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRObservation extends FHIRBaseDomainResource {


    public FHIRCodeableConcept getCategory() {
        return category;
    }

    public void setCategory(FHIRCodeableConcept category) {
        this.category = category;
    }

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public FHIRReference getSubject() {
        return subject;
    }

    public void setSubject(FHIRReference subject) {
        this.subject = subject;
    }

    public FHIRReference getEncounter() {
        return encounter;
    }

    public void setEncounter(FHIRReference encounter) {
        this.encounter = encounter;
    }

    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    public FHIRPeriod getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(FHIRPeriod effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public Collection<FHIRReference> getPerformer() {
        return performer;
    }

    public void setPerformer(Collection<FHIRReference> performer) {
        this.performer = performer;
    }

    public FHIRQuantity getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(FHIRQuantity valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    public FHIRCodeableConcept getValueCodeableConcept() {
        return valueCodeableConcept;
    }

    public void setValueCodeableConcept(FHIRCodeableConcept valueCodeableConcept) {
        this.valueCodeableConcept = valueCodeableConcept;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public FHIRRange getValueRange() {
        return valueRange;
    }

    public void setValueRange(FHIRRange valueRange) {
        this.valueRange = valueRange;
    }

    public FHIRRatio getValueRatio() {
        return valueRatio;
    }

    public void setValueRatio(FHIRRatio valueRatio) {
        this.valueRatio = valueRatio;
    }

    public FHIRSampledData getValueSampledData() {
        return valueSampledData;
    }

    public void setValueSampledData(FHIRSampledData valueSampledData) {
        this.valueSampledData = valueSampledData;
    }

    public FHIRAttachment getValueAttachment() {
        return valueAttachment;
    }

    public void setValueAttachment(FHIRAttachment valueAttachment) {
        this.valueAttachment = valueAttachment;
    }

    public String getValueTime() {
        return valueTime;
    }

    public void setValueTime(String valueTime) {
        this.valueTime = valueTime;
    }

    public String getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(String valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    public FHIRPeriod getValuePeriod() {
        return valuePeriod;
    }

    public void setValuePeriod(FHIRPeriod valuePeriod) {
        this.valuePeriod = valuePeriod;
    }

    public FHIRCodeableConcept getDataAbsentReason() {
        return dataAbsentReason;
    }

    public void setDataAbsentReason(FHIRCodeableConcept dataAbsentReason) {
        this.dataAbsentReason = dataAbsentReason;
    }

    public FHIRCodeableConcept getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(FHIRCodeableConcept interpretation) {
        this.interpretation = interpretation;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public FHIRCodeableConcept getBodySite() {
        return bodySite;
    }

    public void setBodySite(FHIRCodeableConcept bodySite) {
        this.bodySite = bodySite;
    }

    public FHIRCodeableConcept getMethod() {
        return method;
    }

    public void setMethod(FHIRCodeableConcept method) {
        this.method = method;
    }

    public FHIRReference getSpecimen() {
        return specimen;
    }

    public void setSpecimen(FHIRReference specimen) {
        this.specimen = specimen;
    }

    public FHIRReference getDevice() {
        return device;
    }

    public void setDevice(FHIRReference device) {
        this.device = device;
    }

    public Collection<FHIRObservationReferenceRange> getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(Collection<FHIRObservationReferenceRange> referenceRange) {
        this.referenceRange = referenceRange;
    }

    public Collection<FHIRObservationRelated> getRelated() {
        return related;
    }

    public void setRelated(Collection<FHIRObservationRelated> related) {
        this.related = related;
    }

    public Collection<FHIRObservationComponent> getComponent() {
        return component;
    }

    public void setComponent(Collection<FHIRObservationComponent> component) {
        this.component = component;
    }

    public String getResourceType() {
        return resourceType;
    }

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


