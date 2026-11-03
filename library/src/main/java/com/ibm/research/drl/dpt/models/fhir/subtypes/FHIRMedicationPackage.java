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
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

import java.util.Collection;

/** FHIRMedicationPackage FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationPackage {
    /** Constructs a FHIRMedicationPackage. */
    public FHIRMedicationPackage() {}

    private FHIRCodeableConcept container;
    private Collection<FHIRMedicationPackageContent> content;

    /**
     * Returns the content.
     * @return the content
     */
    public Collection<FHIRMedicationPackageContent> getContent() {
        return content;
    }

    /**
     * Sets the content.
     * @param content the content
     */
    public void setContent(Collection<FHIRMedicationPackageContent> content) {
        this.content = content;
    }

    /**
     * Returns the container.
     * @return the container
     */
    public FHIRCodeableConcept getContainer() {
        return container;
    }

    /**
     * Sets the container.
     * @param container the container
     */
    public void setContainer(FHIRCodeableConcept container) {
        this.container = container;
    }

}


