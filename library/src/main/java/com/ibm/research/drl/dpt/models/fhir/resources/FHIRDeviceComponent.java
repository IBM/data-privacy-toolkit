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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRDeviceComponentProductionSpecification;

import java.util.Collection;

/** FHIRDeviceComponent FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDeviceComponent extends FHIRBaseDomainResource {
    /** Constructs a FHIRDeviceComponent. */
    public FHIRDeviceComponent() {}


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

    /**
     * Returns the type.
     * @return the type
     */
    public FHIRCodeableConcept getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the lastSystemChange.
     * @return the lastSystemChange
     */
    public String getLastSystemChange() {
        return lastSystemChange;
    }

    /**
     * Sets the lastSystemChange.
     * @param lastSystemChange the lastSystemChange
     */
    public void setLastSystemChange(String lastSystemChange) {
        this.lastSystemChange = lastSystemChange;
    }

    /**
     * Returns the source.
     * @return the source
     */
    public FHIRReference getSource() {
        return source;
    }

    /**
     * Sets the source.
     * @param source the source
     */
    public void setSource(FHIRReference source) {
        this.source = source;
    }

    /**
     * Returns the parent.
     * @return the parent
     */
    public FHIRReference getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     * @param parent the parent
     */
    public void setParent(FHIRReference parent) {
        this.parent = parent;
    }

    /**
     * Returns the operationalStatus.
     * @return the operationalStatus
     */
    public Collection<FHIRCodeableConcept> getOperationalStatus() {
        return operationalStatus;
    }

    /**
     * Sets the operationalStatus.
     * @param operationalStatus the operationalStatus
     */
    public void setOperationalStatus(Collection<FHIRCodeableConcept> operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    /**
     * Returns the parameterGroup.
     * @return the parameterGroup
     */
    public FHIRCodeableConcept getParameterGroup() {
        return parameterGroup;
    }

    /**
     * Sets the parameterGroup.
     * @param parameterGroup the parameterGroup
     */
    public void setParameterGroup(FHIRCodeableConcept parameterGroup) {
        this.parameterGroup = parameterGroup;
    }

    /**
     * Returns the measurementPrinciple.
     * @return the measurementPrinciple
     */
    public String getMeasurementPrinciple() {
        return measurementPrinciple;
    }

    /**
     * Sets the measurementPrinciple.
     * @param measurementPrinciple the measurementPrinciple
     */
    public void setMeasurementPrinciple(String measurementPrinciple) {
        this.measurementPrinciple = measurementPrinciple;
    }

    /**
     * Returns the productionSpecification.
     * @return the productionSpecification
     */
    public Collection<FHIRDeviceComponentProductionSpecification> getProductionSpecification() {
        return productionSpecification;
    }

    /**
     * Sets the productionSpecification.
     * @param productionSpecification the productionSpecification
     */
    public void setProductionSpecification(Collection<FHIRDeviceComponentProductionSpecification> productionSpecification) {
        this.productionSpecification = productionSpecification;
    }

    /**
     * Returns the languageCode.
     * @return the languageCode
     */
    public FHIRCodeableConcept getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the languageCode.
     * @param languageCode the languageCode
     */
    public void setLanguageCode(FHIRCodeableConcept languageCode) {
        this.languageCode = languageCode;
    }
}
