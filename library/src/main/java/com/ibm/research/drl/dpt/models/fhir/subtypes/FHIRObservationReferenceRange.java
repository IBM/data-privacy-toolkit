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


