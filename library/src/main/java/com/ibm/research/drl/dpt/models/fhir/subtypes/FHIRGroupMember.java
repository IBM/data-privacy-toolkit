/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRPeriod;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGroupMember {
    public FHIRReference getEntity() {
        return entity;
    }

    public void setEntity(FHIRReference entity) {
        this.entity = entity;
    }

    public FHIRPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    private FHIRReference entity;
    private FHIRPeriod period;
    private boolean inactive;
}


