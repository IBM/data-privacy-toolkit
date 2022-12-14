/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGroupCharacteristic {

    private FHIRCodeableConcept code;
    private FHIRCodeableConcept valueCodeableConcept;
    private boolean valueBoolean;
    private FHIRQuantity valueQuantity;
    private FHIRRange valueRange;
    private boolean exclude;
    private FHIRPeriod period;

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public FHIRCodeableConcept getValueCodeableConcept() {
        return valueCodeableConcept;
    }

    public void setValueCodeableConcept(FHIRCodeableConcept valueCodeableConcept) {
        this.valueCodeableConcept = valueCodeableConcept;
    }

    public boolean isValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public FHIRQuantity getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(FHIRQuantity valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    public FHIRRange getValueRange() {
        return valueRange;
    }

    public void setValueRange(FHIRRange valueRange) {
        this.valueRange = valueRange;
    }

    public boolean isExclude() {
        return exclude;
    }

    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }

    public FHIRPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

}


