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
public class FHIRSampledData {

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    public FHIRQuantity getOrigin() {
        return origin;
    }

    public void setOrigin(FHIRQuantity origin) {
        this.origin = origin;
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public float getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(float upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private Collection<FHIRExtension> extension;
    private FHIRQuantity origin;
    private float period;
    private float factor;
    private float lowerLimit;
    private float upperLimit;
    private String dimensions;
    private String data;
}
