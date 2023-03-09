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
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationPackage;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRMedicationProduct;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedication extends FHIRBaseDomainResource {

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public boolean isIsBrand() {
        return isBrand;
    }

    public void setIsBrand(boolean brand) {
        isBrand = brand;
    }

    public FHIRReference getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(FHIRReference manufacturer) {
        this.manufacturer = manufacturer;
    }

    public FHIRMedicationProduct getProduct() {
        return product;
    }

    public void setProduct(FHIRMedicationProduct product) {
        this.product = product;
    }

    public FHIRMedicationPackage getPackage() {
        return Package;
    }

    public void setPackage(FHIRMedicationPackage aPackage) {
        Package = aPackage;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isBrand() {
        return isBrand;
    }

    public void setBrand(boolean brand) {
        isBrand = brand;
    }

    private String resourceType;
    private FHIRCodeableConcept code;
    private boolean isBrand;
    private FHIRReference manufacturer;
    private FHIRMedicationProduct product;
    private FHIRMedicationPackage Package;

}


