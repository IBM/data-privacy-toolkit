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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;

import java.util.Collection;

/** FHIRIdentifier FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRIdentifier {
    /** Constructs a FHIRIdentifier. */
    public FHIRIdentifier() {}


    private Collection<FHIRExtension> extension;
    private String use;
    private FHIRCodeableConcept type;
    private String system;
    private String value;
    private FHIRPeriod period;
    private FHIRReference assigner;

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
     * Returns the use.
     * @return the use
     */
    public String getUse() {
        return use;
    }

    /**
     * Sets the use.
     * @param use the use
     */
    public void setUse(String use) {
        this.use = use;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public FHIRCodeableConcept getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(FHIRCodeableConcept type) {
        this.type = type;
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
     * Returns the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public FHIRPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    /**
     * Returns the assigner.
     * @return the assigner
     */
    public FHIRReference getAssigner() {
        return assigner;
    }

    /**
     * Sets the assigner.
     * @param assigner the assigner
     */
    public void setAssigner(FHIRReference assigner) {
        this.assigner = assigner;
    }

}
