/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
