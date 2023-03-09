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


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationOrderDispenseRequest {

    public FHIRCodeableConcept getMedicationCodeableConcept() {
        return medicationCodeableConcept;
    }

    public void setMedicationCodeableConcept(FHIRCodeableConcept medicationCodeableConcept) {
        this.medicationCodeableConcept = medicationCodeableConcept;
    }

    public FHIRReference getMedicationReference() {
        return medicationReference;
    }

    public void setMedicationReference(FHIRReference medicationReference) {
        this.medicationReference = medicationReference;
    }

    public FHIRPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(FHIRPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getNumberOfRepeatsAllowed() {
        return numberOfRepeatsAllowed;
    }

    public void setNumberOfRepeatsAllowed(String numberOfRepeatsAllowed) {
        this.numberOfRepeatsAllowed = numberOfRepeatsAllowed;
    }

    public FHIRQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(FHIRQuantity quantity) {
        this.quantity = quantity;
    }

    public FHIRQuantity getExpectedSupplyDuration() {
        return expectedSupplyDuration;
    }

    public void setExpectedSupplyDuration(FHIRQuantity expectedSupplyDuration) {
        this.expectedSupplyDuration = expectedSupplyDuration;
    }

    private FHIRCodeableConcept medicationCodeableConcept;
    private FHIRReference medicationReference;
    private FHIRPeriod validityPeriod;
    private String numberOfRepeatsAllowed;
    private FHIRQuantity quantity;
    private FHIRQuantity expectedSupplyDuration;

}


