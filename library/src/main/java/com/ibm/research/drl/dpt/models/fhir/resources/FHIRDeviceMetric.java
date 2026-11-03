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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCalibration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRTiming;

import java.util.Collection;

/** FHIRDeviceMetric FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDeviceMetric extends FHIRBaseDomainResource {
    /** Constructs a FHIRDeviceMetric. */
    public FHIRDeviceMetric() {}


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
     * Returns the unit.
     * @return the unit
     */
    public FHIRCodeableConcept getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     * @param unit the unit
     */
    public void setUnit(FHIRCodeableConcept unit) {
        this.unit = unit;
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
    public String getOperationalStatus() {
        return operationalStatus;
    }

    /**
     * Sets the operationalStatus.
     * @param operationalStatus the operationalStatus
     */
    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    /**
     * Returns the color.
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color.
     * @param color the color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns the category.
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     * @param category the category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the measurementPeriod.
     * @return the measurementPeriod
     */
    public FHIRTiming getMeasurementPeriod() {
        return measurementPeriod;
    }

    /**
     * Sets the measurementPeriod.
     * @param measurementPeriod the measurementPeriod
     */
    public void setMeasurementPeriod(FHIRTiming measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    /**
     * Returns the calibration.
     * @return the calibration
     */
    public Collection<FHIRCalibration> getCalibration() {
        return calibration;
    }

    /**
     * Sets the calibration.
     * @param calibration the calibration
     */
    public void setCalibration(Collection<FHIRCalibration> calibration) {
        this.calibration = calibration;
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

    private String resourceType;

    private FHIRCodeableConcept type;
    private FHIRIdentifier identifier;
    private FHIRCodeableConcept unit;
    private FHIRReference source;
    private FHIRReference parent;
    private String operationalStatus;
    private String color;
    private String category;
    private FHIRTiming measurementPeriod;
    private Collection<FHIRCalibration> calibration;
}
