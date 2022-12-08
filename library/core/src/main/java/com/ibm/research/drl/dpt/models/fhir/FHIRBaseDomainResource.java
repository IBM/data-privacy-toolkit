/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/

package com.ibm.research.drl.dpt.models.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRBaseDomainResource extends FHIRResource {

    public FHIRNarrative getText() {
        return text;
    }

    public void setText(FHIRNarrative text) {
        this.text = text;
    }

    public Collection<FHIRResource> getContained() {
        return contained;
    }

    public void setContained(Collection<FHIRResource> contained) {
        this.contained = contained;
    }

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    public Collection<FHIRExtension> getModifierExtension() {
        return modifierExtension;
    }

    public void setModifierExtension(Collection<FHIRExtension> modifierExtension) {
        this.modifierExtension = modifierExtension;
    }

    private FHIRNarrative text;
    private Collection<FHIRResource> contained;
    private Collection<FHIRExtension> extension;
    private Collection<FHIRExtension> modifierExtension;
}


