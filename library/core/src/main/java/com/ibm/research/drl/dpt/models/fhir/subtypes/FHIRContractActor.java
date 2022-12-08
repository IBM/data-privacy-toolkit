/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractActor {
    public FHIRReference getEntity() {
        return entity;
    }

    public void setEntity(FHIRReference entity) {
        this.entity = entity;
    }

    public Collection<FHIRCodeableConcept> getRole() {
        return role;
    }

    public void setRole(Collection<FHIRCodeableConcept> role) {
        this.role = role;
    }

    private FHIRReference entity;
    private Collection<FHIRCodeableConcept> role;
}


