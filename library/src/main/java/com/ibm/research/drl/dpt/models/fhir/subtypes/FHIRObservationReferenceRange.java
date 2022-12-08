
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRObservationReferenceRange {


    public FHIRQuantity getLow() {
        return low;
    }

    public void setLow(FHIRQuantity low) {
        this.low = low;
    }

    public FHIRQuantity getHigh() {
        return high;
    }

    public void setHigh(FHIRQuantity high) {
        this.high = high;
    }

    public FHIRCodeableConcept getMeaning() {
        return meaning;
    }

    public void setMeaning(FHIRCodeableConcept meaning) {
        this.meaning = meaning;
    }

    public FHIRRange getAge() {
        return age;
    }

    public void setAge(FHIRRange age) {
        this.age = age;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private FHIRQuantity low;
    private FHIRQuantity high;
    private FHIRCodeableConcept meaning;
    private FHIRRange age;
    private String text;

}


