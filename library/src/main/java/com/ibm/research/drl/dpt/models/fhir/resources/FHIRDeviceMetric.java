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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDeviceMetric extends FHIRBaseDomainResource {

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

    public FHIRCodeableConcept getUnit() {
        return unit;
    }

    public void setUnit(FHIRCodeableConcept unit) {
        this.unit = unit;
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

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public FHIRTiming getMeasurementPeriod() {
        return measurementPeriod;
    }

    public void setMeasurementPeriod(FHIRTiming measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    public Collection<FHIRCalibration> getCalibration() {
        return calibration;
    }

    public void setCalibration(Collection<FHIRCalibration> calibration) {
        this.calibration = calibration;
    }

    public String getResourceType() {
        return resourceType;
    }

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
