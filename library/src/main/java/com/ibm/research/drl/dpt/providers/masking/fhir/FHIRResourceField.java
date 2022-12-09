/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

public class FHIRResourceField {

    private final String path;
    private final String fhirType;

    public String getPath() {
        return path;
    }

    public String getFhirType() {
        return fhirType;
    }

    public FHIRResourceField(String path, String fhirType) {
        this.path = path;
        this.fhirType = fhirType;
    }
}


