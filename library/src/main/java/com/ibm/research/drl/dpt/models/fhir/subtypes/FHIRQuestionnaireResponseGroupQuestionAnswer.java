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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;

import java.util.Collection;

/** FHIRQuestionnaireResponseGroupQuestionAnswer FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroupQuestionAnswer {
    /** Constructs a FHIRQuestionnaireResponseGroupQuestionAnswer. */
    public FHIRQuestionnaireResponseGroupQuestionAnswer() {}


    private boolean valueBoolean;
    private float valueDecimal;
    private int valueInteger;
    private String valueDate;
    private String valueDateTime;
    private String valueInstant;
    private String valueTime;
    private String valueString;
    private String valueUri;
    private FHIRAttachment valueAttachment;
    private FHIRCoding valueCoding;
    private FHIRQuantity valueQuantity;
    private FHIRReference valueReference;
    private Collection<FHIRQuestionnaireResponseGroup> group;

    /**
     * Returns the valueBoolean.
     * @return the valueBoolean
     */
    public boolean isValueBoolean() {
        return valueBoolean;
    }

    /**
     * Sets the valueBoolean.
     * @param valueBoolean the valueBoolean
     */
    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    /**
     * Returns the valueDecimal.
     * @return the valueDecimal
     */
    public float getValueDecimal() {
        return valueDecimal;
    }

    /**
     * Sets the valueDecimal.
     * @param valueDecimal the valueDecimal
     */
    public void setValueDecimal(float valueDecimal) {
        this.valueDecimal = valueDecimal;
    }

    /**
     * Returns the valueInteger.
     * @return the valueInteger
     */
    public int getValueInteger() {
        return valueInteger;
    }

    /**
     * Sets the valueInteger.
     * @param valueInteger the valueInteger
     */
    public void setValueInteger(int valueInteger) {
        this.valueInteger = valueInteger;
    }

    /**
     * Returns the valueDate.
     * @return the valueDate
     */
    public String getValueDate() {
        return valueDate;
    }

    /**
     * Sets the valueDate.
     * @param valueDate the valueDate
     */
    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
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
     * Returns the valueInstant.
     * @return the valueInstant
     */
    public String getValueInstant() {
        return valueInstant;
    }

    /**
     * Sets the valueInstant.
     * @param valueInstant the valueInstant
     */
    public void setValueInstant(String valueInstant) {
        this.valueInstant = valueInstant;
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
     * Returns the valueUri.
     * @return the valueUri
     */
    public String getValueUri() {
        return valueUri;
    }

    /**
     * Sets the valueUri.
     * @param valueUri the valueUri
     */
    public void setValueUri(String valueUri) {
        this.valueUri = valueUri;
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
     * Returns the valueCoding.
     * @return the valueCoding
     */
    public FHIRCoding getValueCoding() {
        return valueCoding;
    }

    /**
     * Sets the valueCoding.
     * @param valueCoding the valueCoding
     */
    public void setValueCoding(FHIRCoding valueCoding) {
        this.valueCoding = valueCoding;
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
     * Returns the valueReference.
     * @return the valueReference
     */
    public FHIRReference getValueReference() {
        return valueReference;
    }

    /**
     * Sets the valueReference.
     * @param valueReference the valueReference
     */
    public void setValueReference(FHIRReference valueReference) {
        this.valueReference = valueReference;
    }

    /**
     * Returns the group.
     * @return the group
     */
    public Collection<FHIRQuestionnaireResponseGroup> getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group the group
     */
    public void setGroup(Collection<FHIRQuestionnaireResponseGroup> group) {
        this.group = group;
    }

}


