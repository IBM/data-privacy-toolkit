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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRTiming;

import java.util.Collection;

/** FHIRCarePlanActivityDetail FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCarePlanActivityDetail {
    /** Constructs a FHIRCarePlanActivityDetail. */
    public FHIRCarePlanActivityDetail() {}


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
     * Returns the reasonCode.
     * @return the reasonCode
     */
    public Collection<FHIRCodeableConcept> getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the reasonCode.
     * @param reasonCode the reasonCode
     */
    public void setReasonCode(Collection<FHIRCodeableConcept> reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Returns the reasonReference.
     * @return the reasonReference
     */
    public Collection<FHIRReference> getReasonReference() {
        return reasonReference;
    }

    /**
     * Sets the reasonReference.
     * @param reasonReference the reasonReference
     */
    public void setReasonReference(Collection<FHIRReference> reasonReference) {
        this.reasonReference = reasonReference;
    }

    /**
     * Returns the goal.
     * @return the goal
     */
    public Collection<FHIRReference> getGoal() {
        return goal;
    }

    /**
     * Sets the goal.
     * @param goal the goal
     */
    public void setGoal(Collection<FHIRReference> goal) {
        this.goal = goal;
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
     * Returns the statusReason.
     * @return the statusReason
     */
    public FHIRCodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * Sets the statusReason.
     * @param statusReason the statusReason
     */
    public void setStatusReason(FHIRCodeableConcept statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * Returns the prohibited.
     * @return the prohibited
     */
    public boolean isProhibited() {
        return prohibited;
    }

    /**
     * Sets the prohibited.
     * @param prohibited the prohibited
     */
    public void setProhibited(boolean prohibited) {
        this.prohibited = prohibited;
    }

    /**
     * Returns the scheduledTiming.
     * @return the scheduledTiming
     */
    public FHIRTiming getScheduledTiming() {
        return scheduledTiming;
    }

    /**
     * Sets the scheduledTiming.
     * @param scheduledTiming the scheduledTiming
     */
    public void setScheduledTiming(FHIRTiming scheduledTiming) {
        this.scheduledTiming = scheduledTiming;
    }

    /**
     * Returns the scheduledPeriod.
     * @return the scheduledPeriod
     */
    public FHIRPeriod getScheduledPeriod() {
        return scheduledPeriod;
    }

    /**
     * Sets the scheduledPeriod.
     * @param scheduledPeriod the scheduledPeriod
     */
    public void setScheduledPeriod(FHIRPeriod scheduledPeriod) {
        this.scheduledPeriod = scheduledPeriod;
    }

    /**
     * Returns the scheduledString.
     * @return the scheduledString
     */
    public String getScheduledString() {
        return scheduledString;
    }

    /**
     * Sets the scheduledString.
     * @param scheduledString the scheduledString
     */
    public void setScheduledString(String scheduledString) {
        this.scheduledString = scheduledString;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public FHIRReference getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * @param location the location
     */
    public void setLocation(FHIRReference location) {
        this.location = location;
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
     * Returns the productCodeableConcept.
     * @return the productCodeableConcept
     */
    public FHIRCodeableConcept getProductCodeableConcept() {
        return productCodeableConcept;
    }

    /**
     * Sets the productCodeableConcept.
     * @param productCodeableConcept the productCodeableConcept
     */
    public void setProductCodeableConcept(FHIRCodeableConcept productCodeableConcept) {
        this.productCodeableConcept = productCodeableConcept;
    }

    /**
     * Returns the productReference.
     * @return the productReference
     */
    public FHIRReference getProductReference() {
        return productReference;
    }

    /**
     * Sets the productReference.
     * @param productReference the productReference
     */
    public void setProductReference(FHIRReference productReference) {
        this.productReference = productReference;
    }

    /**
     * Returns the dailyAmount.
     * @return the dailyAmount
     */
    public FHIRQuantity getDailyAmount() {
        return dailyAmount;
    }

    /**
     * Sets the dailyAmount.
     * @param dailyAmount the dailyAmount
     */
    public void setDailyAmount(FHIRQuantity dailyAmount) {
        this.dailyAmount = dailyAmount;
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
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description
     */
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


