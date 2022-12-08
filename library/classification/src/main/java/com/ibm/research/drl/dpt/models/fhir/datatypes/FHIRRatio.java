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
public class FHIRRatio {
    private Collection<FHIRExtension> extension;
    private FHIRQuantity numerator;
    private FHIRQuantity denominator;

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    public FHIRQuantity getNumerator() {
        return numerator;
    }

    public void setNumerator(FHIRQuantity numerator) {
        this.numerator = numerator;
    }

    public FHIRQuantity getDenominator() {
        return denominator;
    }

    public void setDenominator(FHIRQuantity denominator) {
        this.denominator = denominator;
    }

}
