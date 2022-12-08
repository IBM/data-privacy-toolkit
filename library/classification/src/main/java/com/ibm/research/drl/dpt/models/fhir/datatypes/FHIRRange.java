/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRRange {
    private Collection<FHIRExtension> extension;
    private FHIRQuantity low;
    private FHIRQuantity high;

    public FHIRQuantity getHigh() {
        return high;
    }

    public void setHigh(FHIRQuantity high) {
        this.high = high;
    }

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    public FHIRQuantity getLow() {
        return low;
    }

    public void setLow(FHIRQuantity low) {
        this.low = low;
    }

}
