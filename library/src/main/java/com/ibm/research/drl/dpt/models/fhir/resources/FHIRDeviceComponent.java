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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRDeviceComponentProductionSpecification;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDeviceComponent extends FHIRBaseDomainResource {

    private FHIRCodeableConcept type;
    private FHIRIdentifier identifier;
    private String lastSystemChange;
    private FHIRReference source;
    private FHIRReference parent;
    private Collection<FHIRCodeableConcept> operationalStatus;
    private FHIRCodeableConcept parameterGroup;
    private String measurementPrinciple;
    private Collection<FHIRDeviceComponentProductionSpecification> productionSpecification;
    private FHIRCodeableConcept languageCode;

    public FHIRCodeableConcept getType() {
        return type;
    }

    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getLastSystemChange() {
        return lastSystemChange;
    }

    public void setLastSystemChange(String lastSystemChange) {
        this.lastSystemChange = lastSystemChange;
    }

    public FHIRReference getSource() {
        return source;
    }

    public void setSource(FHIRReference source) {
        this.source = source;
    }

    public FHIRReference getParent() {
        return parent;
    }

    public void setParent(FHIRReference parent) {
        this.parent = parent;
    }

    public Collection<FHIRCodeableConcept> getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(Collection<FHIRCodeableConcept> operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public FHIRCodeableConcept getParameterGroup() {
        return parameterGroup;
    }

    public void setParameterGroup(FHIRCodeableConcept parameterGroup) {
        this.parameterGroup = parameterGroup;
    }

    public String getMeasurementPrinciple() {
        return measurementPrinciple;
    }

    public void setMeasurementPrinciple(String measurementPrinciple) {
        this.measurementPrinciple = measurementPrinciple;
    }

    public Collection<FHIRDeviceComponentProductionSpecification> getProductionSpecification() {
        return productionSpecification;
    }

    public void setProductionSpecification(Collection<FHIRDeviceComponentProductionSpecification> productionSpecification) {
        this.productionSpecification = productionSpecification;
    }

    public FHIRCodeableConcept getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(FHIRCodeableConcept languageCode) {
        this.languageCode = languageCode;
    }
}
