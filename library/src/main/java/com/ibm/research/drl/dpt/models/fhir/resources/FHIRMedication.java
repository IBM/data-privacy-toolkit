/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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


