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


/** FHIRMedication FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedication extends FHIRBaseDomainResource {
    /** Constructs a FHIRMedication. */
    public FHIRMedication() {}


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
     * Returns the isBrand.
     * @return the isBrand
     */
    public boolean isIsBrand() {
        return isBrand;
    }

    /**
     * Sets the isBrand.
     * @param brand the isBrand
     */
    public void setIsBrand(boolean brand) {
        isBrand = brand;
    }

    /**
     * Returns the manufacturer.
     * @return the manufacturer
     */
    public FHIRReference getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer.
     * @param manufacturer the manufacturer
     */
    public void setManufacturer(FHIRReference manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the product.
     * @return the product
     */
    public FHIRMedicationProduct getProduct() {
        return product;
    }

    /**
     * Sets the product.
     * @param product the product
     */
    public void setProduct(FHIRMedicationProduct product) {
        this.product = product;
    }

    /**
     * Returns the package.
     * @return the package
     */
    public FHIRMedicationPackage getPackage() {
        return Package;
    }

    /**
     * Sets the package.
     * @param aPackage the package
     */
    public void setPackage(FHIRMedicationPackage aPackage) {
        Package = aPackage;
    }

    /**
     * Returns the resourceType.
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resourceType.
     * @param resourceType the resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Returns the brand.
     * @return the brand
     */
    public boolean isBrand() {
        return isBrand;
    }

    /**
     * Sets the brand.
     * @param brand the brand
     */
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


