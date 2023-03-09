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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRQuantity;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRange;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRatio;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationAdministrationDosage {
    private String text;
    private FHIRCodeableConcept siteCodeableConcept;
    private FHIRReference siteReference;
    private FHIRCodeableConcept route;
    private FHIRCodeableConcept method;
    private FHIRQuantity quantity;
    private FHIRRatio rateRatio;
    private FHIRRange rateRange;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FHIRCodeableConcept getSiteCodeableConcept() {
        return siteCodeableConcept;
    }

    public void setSiteCodeableConcept(FHIRCodeableConcept siteCodeableConcept) {
        this.siteCodeableConcept = siteCodeableConcept;
    }

    public FHIRReference getSiteReference() {
        return siteReference;
    }

    public void setSiteReference(FHIRReference siteReference) {
        this.siteReference = siteReference;
    }

    public FHIRCodeableConcept getRoute() {
        return route;
    }

    public void setRoute(FHIRCodeableConcept route) {
        this.route = route;
    }

    public FHIRCodeableConcept getMethod() {
        return method;
    }

    public void setMethod(FHIRCodeableConcept method) {
        this.method = method;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public FHIRRatio getRateRatio() {
        return rateRatio;
    }

    public void setRateRatio(FHIRRatio rateRatio) {
        this.rateRatio = rateRatio;
    }

    public FHIRRange getRateRange() {
        return rateRange;
    }

    public void setRateRange(FHIRRange rateRange) {
        this.rateRange = rateRange;
    }
}


