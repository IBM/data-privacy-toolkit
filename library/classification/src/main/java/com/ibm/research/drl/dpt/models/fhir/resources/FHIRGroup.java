/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRGroupCharacteristic;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRGroupMember;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGroup extends FHIRBaseDomainResource {

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Collection<FHIRGroupCharacteristic> getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(Collection<FHIRGroupCharacteristic> characteristic) {
        this.characteristic = characteristic;
    }

    public Collection<FHIRGroupMember> getMember() {
        return member;
    }

    public void setMember(Collection<FHIRGroupMember> member) {
        this.member = member;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String resourceType;
    private Collection<FHIRIdentifier> identifier;
    private String type;
    private boolean actual;
    private FHIRCodeableConcept code;
    private String name;
    private String quantity;
    private Collection<FHIRGroupCharacteristic> characteristic;
    private Collection<FHIRGroupMember> member;
}
