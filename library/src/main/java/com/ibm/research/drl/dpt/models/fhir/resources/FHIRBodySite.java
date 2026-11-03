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
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

/** FHIRBodySite FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRBodySite extends FHIRBaseDomainResource {
    /** Constructs a FHIRBodySite. */
    public FHIRBodySite() {}


    /**
     * Returns the resourceType.
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resourceType.
     * @param resourceType the resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String resourceType;
    private FHIRReference patient;
    private Collection<FHIRIdentifier> identifier;
    private FHIRCodeableConcept code;
    private Collection<FHIRCodeableConcept> modifier;
    private String description;
    private Collection<FHIRAttachment> image;

    /**
     * Returns the patient.
     * @return the patient
     */
    public FHIRReference getPatient() {
        return patient;
    }

    /**
     * Sets the patient.
     * @param patient the patient
     */
    public void setPatient(FHIRReference patient) {
        this.patient = patient;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier
     */
    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the code.
     * @return the code
     */
    public FHIRCodeableConcept getCode() {
        return code;
    }

    /**
     * Sets the code.
     * @param code the code
     */
    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    /**
     * Returns the modifier.
     * @return the modifier
     */
    public Collection<FHIRCodeableConcept> getModifier() {
        return modifier;
    }

    /**
     * Sets the modifier.
     * @param modifier the modifier
     */
    public void setModifier(Collection<FHIRCodeableConcept> modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the image.
     * @return the image
     */
    public Collection<FHIRAttachment> getImage() {
        return image;
    }

    /**
     * Sets the image.
     * @param image the image
     */
    public void setImage(Collection<FHIRAttachment> image) {
        this.image = image;
    }


}
