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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaireResponseGroupQuestionAnswer {

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

    public boolean isValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public float getValueDecimal() {
        return valueDecimal;
    }

    public void setValueDecimal(float valueDecimal) {
        this.valueDecimal = valueDecimal;
    }

    public int getValueInteger() {
        return valueInteger;
    }

    public void setValueInteger(int valueInteger) {
        this.valueInteger = valueInteger;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(String valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    public String getValueInstant() {
        return valueInstant;
    }

    public void setValueInstant(String valueInstant) {
        this.valueInstant = valueInstant;
    }

    public String getValueTime() {
        return valueTime;
    }

    public void setValueTime(String valueTime) {
        this.valueTime = valueTime;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public String getValueUri() {
        return valueUri;
    }

    public void setValueUri(String valueUri) {
        this.valueUri = valueUri;
    }

    public FHIRAttachment getValueAttachment() {
        return valueAttachment;
    }

    public void setValueAttachment(FHIRAttachment valueAttachment) {
        this.valueAttachment = valueAttachment;
    }

    public FHIRCoding getValueCoding() {
        return valueCoding;
    }

    public void setValueCoding(FHIRCoding valueCoding) {
        this.valueCoding = valueCoding;
    }

    public FHIRQuantity getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(FHIRQuantity valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    public FHIRReference getValueReference() {
        return valueReference;
    }

    public void setValueReference(FHIRReference valueReference) {
        this.valueReference = valueReference;
    }

    public Collection<FHIRQuestionnaireResponseGroup> getGroup() {
        return group;
    }

    public void setGroup(Collection<FHIRQuestionnaireResponseGroup> group) {
        this.group = group;
    }

}


