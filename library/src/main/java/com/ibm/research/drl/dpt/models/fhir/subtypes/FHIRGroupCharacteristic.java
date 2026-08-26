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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;


/** FHIRGroupCharacteristic FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGroupCharacteristic {
    /** Constructs a FHIRGroupCharacteristic. */
    public FHIRGroupCharacteristic() {}


    private FHIRCodeableConcept code;
    private FHIRCodeableConcept valueCodeableConcept;
    private boolean valueBoolean;
    private FHIRQuantity valueQuantity;
    private FHIRRange valueRange;
    private boolean exclude;
    private FHIRPeriod period;

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
     * Returns the exclude.
     * @return the exclude
     */
    public boolean isExclude() {
        return exclude;
    }

    /**
     * Sets the exclude.
     * @param exclude the exclude
     */
    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public FHIRPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

}


