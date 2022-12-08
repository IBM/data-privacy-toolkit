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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAddress;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIROrganizationContact;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIROrganization extends FHIRBaseDomainResource {

    private Collection<FHIRIdentifier> identifier;
    private boolean active;
    private FHIRCodeableConcept type;
    private String name;
    private Collection<FHIRContactPoint> telecom;
    private Collection<FHIRAddress> address;
    private FHIRReference partOf;
    private Collection<FHIROrganizationContact> contact;

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FHIRCodeableConcept getType() {
        return type;
    }

    public void setType(FHIRCodeableConcept type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    public Collection<FHIRAddress> getAddress() {
        return address;
    }

    public void setAddress(Collection<FHIRAddress> address) {
        this.address = address;
    }

    public FHIRReference getPartOf() {
        return partOf;
    }

    public void setPartOf(FHIRReference partOf) {
        this.partOf = partOf;
    }

    public Collection<FHIROrganizationContact> getContact() {
        return contact;
    }

    public void setContact(Collection<FHIROrganizationContact> contact) {
        this.contact = contact;
    }
}
