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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRTiming;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlanActivityDetail {

    public FHIRCodeableConcept getCategory() {
        return category;
    }

    public void setCategory(FHIRCodeableConcept category) {
        this.category = category;
    }

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public Collection<FHIRCodeableConcept> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Collection<FHIRCodeableConcept> reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Collection<FHIRReference> getReasonReference() {
        return reasonReference;
    }

    public void setReasonReference(Collection<FHIRReference> reasonReference) {
        this.reasonReference = reasonReference;
    }

    public Collection<FHIRReference> getGoal() {
        return goal;
    }

    public void setGoal(Collection<FHIRReference> goal) {
        this.goal = goal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FHIRCodeableConcept getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(FHIRCodeableConcept statusReason) {
        this.statusReason = statusReason;
    }

    public boolean isProhibited() {
        return prohibited;
    }

    public void setProhibited(boolean prohibited) {
        this.prohibited = prohibited;
    }

    public FHIRTiming getScheduledTiming() {
        return scheduledTiming;
    }

    public void setScheduledTiming(FHIRTiming scheduledTiming) {
        this.scheduledTiming = scheduledTiming;
    }

    public FHIRPeriod getScheduledPeriod() {
        return scheduledPeriod;
    }

    public void setScheduledPeriod(FHIRPeriod scheduledPeriod) {
        this.scheduledPeriod = scheduledPeriod;
    }

    public String getScheduledString() {
        return scheduledString;
    }

    public void setScheduledString(String scheduledString) {
        this.scheduledString = scheduledString;
    }

    public FHIRReference getLocation() {
        return location;
    }

    public void setLocation(FHIRReference location) {
        this.location = location;
    }

    public Collection<FHIRReference> getPerformer() {
        return performer;
    }

    public void setPerformer(Collection<FHIRReference> performer) {
        this.performer = performer;
    }

    public FHIRCodeableConcept getProductCodeableConcept() {
        return productCodeableConcept;
    }

    public void setProductCodeableConcept(FHIRCodeableConcept productCodeableConcept) {
        this.productCodeableConcept = productCodeableConcept;
    }

    public FHIRReference getProductReference() {
        return productReference;
    }

    public void setProductReference(FHIRReference productReference) {
        this.productReference = productReference;
    }

    public FHIRQuantity getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(FHIRQuantity dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private FHIRCodeableConcept category;
    private FHIRCodeableConcept code;
    private Collection<FHIRCodeableConcept> reasonCode;
    private Collection<FHIRReference> reasonReference;
    private Collection<FHIRReference> goal;
    private String status;
    private FHIRCodeableConcept statusReason;
    private boolean prohibited;
    private FHIRTiming scheduledTiming;
    private FHIRPeriod scheduledPeriod;
    private String scheduledString;
    private FHIRReference location;
    private Collection<FHIRReference> performer;
    private FHIRCodeableConcept productCodeableConcept;
    private FHIRReference productReference;
    private FHIRQuantity dailyAmount;
    private FHIRQuantity quantity;
    private String description;
}


