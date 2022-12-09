/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;

import java.io.Serializable;

public class FHIRNarrativeMaskingProvider implements Serializable {

    private final boolean removeDiv;
    private final boolean removeExtensions;

    public FHIRNarrativeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this.removeDiv = maskingConfiguration.getBooleanValue("fhir.narrative.removeDiv");
        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.narrative.removeExtensions");
    }

    public FHIRNarrative mask(FHIRNarrative narrative) {
        if (narrative == null) {
            return null;
        }

        if (this.removeDiv) {
            narrative.setDiv(null);
        }

        if (this.removeExtensions) {
            narrative.setExtension(null);
        }

        return narrative;
    }
}


