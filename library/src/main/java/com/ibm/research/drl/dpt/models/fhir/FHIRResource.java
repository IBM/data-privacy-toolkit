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
package com.ibm.research.drl.dpt.models.fhir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRMeta;

/** FHIRResource FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRResource {
    /** Constructs a FHIRResource. */
    public FHIRResource() {}


    private String id;
    private FHIRMeta meta;
    private String implicitRules;
    private String language;

    /**
     * Returns the id.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the meta.
     * @return the meta
     */
    public FHIRMeta getMeta() {
        return meta;
    }

    /**
     * Sets the meta.
     * @param meta the meta
     */
    public void setMeta(FHIRMeta meta) {
        this.meta = meta;
    }

    /**
     * Returns the implicitRules.
     * @return the implicitRules
     */
    public String getImplicitRules() {
        return implicitRules;
    }

    /**
     * Sets the implicitRules.
     * @param implicitRules the implicitRules
     */
    public void setImplicitRules(String implicitRules) {
        this.implicitRules = implicitRules;
    }

    /**
     * Returns the language.
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

}
