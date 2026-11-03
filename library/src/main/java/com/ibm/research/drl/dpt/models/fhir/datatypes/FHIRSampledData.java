/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;

import java.util.Collection;

/** FHIRSampledData FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRSampledData {
    /** Constructs a FHIRSampledData. */
    public FHIRSampledData() {}


    /**
     * Returns the extension.
     * @return the extension
     */
    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     * @param extension the extension
     */
    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    /**
     * Returns the origin.
     * @return the origin
     */
    public FHIRQuantity getOrigin() {
        return origin;
    }

    /**
     * Sets the origin.
     * @param origin the origin
     */
    public void setOrigin(FHIRQuantity origin) {
        this.origin = origin;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public float getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(float period) {
        this.period = period;
    }

    /**
     * Returns the factor.
     * @return the factor
     */
    public float getFactor() {
        return factor;
    }

    /**
     * Sets the factor.
     * @param factor the factor
     */
    public void setFactor(float factor) {
        this.factor = factor;
    }

    /**
     * Returns the lowerLimit.
     * @return the lowerLimit
     */
    public float getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Sets the lowerLimit.
     * @param lowerLimit the lowerLimit
     */
    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * Returns the upperLimit.
     * @return the upperLimit
     */
    public float getUpperLimit() {
        return upperLimit;
    }

    /**
     * Sets the upperLimit.
     * @param upperLimit the upperLimit
     */
    public void setUpperLimit(float upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * Returns the dimensions.
     * @return the dimensions
     */
    public String getDimensions() {
        return dimensions;
    }

    /**
     * Sets the dimensions.
     * @param dimensions the dimensions
     */
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Returns the data.
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data.
     * @param data the data
     */
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
