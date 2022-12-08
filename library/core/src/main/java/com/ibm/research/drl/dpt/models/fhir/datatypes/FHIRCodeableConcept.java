/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCodeableConcept {
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<FHIRCoding> getCoding() {
        return coding;
    }

    public void setCoding(Collection<FHIRCoding> coding) {
        this.coding = coding;
    }

    private String text;
    private Collection<FHIRCoding> coding;
}
