/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAddress;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRHumanName;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIROrganizationContact {

    private FHIRCodeableConcept purpose;
    private FHIRHumanName name;
    private Collection<FHIRContactPoint> telecom;
    private FHIRAddress address;

    public FHIRCodeableConcept getPurpose() {
        return purpose;
    }

    public void setPurpose(FHIRCodeableConcept purpose) {
        this.purpose = purpose;
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
}


