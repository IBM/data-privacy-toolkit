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

/** FHIRQuantity FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuantity {
    /** Constructs a FHIRQuantity. */
    public FHIRQuantity() {}


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
     * Returns the value.
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Returns the comparator.
     * @return the comparator
     */
    public String getComparator() {
        return comparator;
    }

    /**
     * Sets the comparator.
     * @param comparator the comparator
     */
    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns the unit.
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     * @param unit the unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns the system.
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the system.
     * @param system the system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Returns the code.
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    private Collection<FHIRExtension> extension;
    private float value;
    private String comparator;
    private String unit;
    private String system;
    private String code;
}
