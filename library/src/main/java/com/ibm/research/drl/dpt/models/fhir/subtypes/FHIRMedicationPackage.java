/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationPackage {
    private FHIRCodeableConcept container;
    private Collection<FHIRMedicationPackageContent> content;

    public Collection<FHIRMedicationPackageContent> getContent() {
        return content;
    }

    public void setContent(Collection<FHIRMedicationPackageContent> content) {
        this.content = content;
    }

    public FHIRCodeableConcept getContainer() {
        return container;
    }

    public void setContainer(FHIRCodeableConcept container) {
        this.container = container;
    }

}


