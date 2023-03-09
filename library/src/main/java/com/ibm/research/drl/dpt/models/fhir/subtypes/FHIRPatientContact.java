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
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPatientContact {

    public Collection<FHIRCodeableConcept> getRelationship() {
        return relationship;
    }

    public void setRelationship(Collection<FHIRCodeableConcept> relationship) {
        this.relationship = relationship;
    }

    public FHIRHumanName getName() {
        return name;
    }

    public void setName(FHIRHumanName name) {
        this.name = name;
    }

    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    public FHIRAddress getAddress() {
        return address;
    }

    public void setAddress(FHIRAddress address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public FHIRReference getOrganization() {
        return organization;
    }

    public void setOrganization(FHIRReference organization) {
        this.organization = organization;
    }

    public FHIRPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    private Collection<FHIRCodeableConcept> relationship;
    private FHIRHumanName name;
    private Collection<FHIRContactPoint> telecom;
    private FHIRAddress address;
    private String gender;
    private FHIRReference organization;
    private FHIRPeriod period;
}
