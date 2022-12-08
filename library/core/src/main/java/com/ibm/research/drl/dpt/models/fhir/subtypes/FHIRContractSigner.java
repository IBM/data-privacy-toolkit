/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRContractSigner {

    private FHIRCoding type;
    private FHIRReference party;
    private String signature;

    public FHIRCoding getType() {
        return type;
    }

    public void setType(FHIRCoding type) {
        this.type = type;
    }

    public FHIRReference getParty() {
        return party;
    }

    public void setParty(FHIRReference party) {
        this.party = party;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}


