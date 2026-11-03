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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRQuestionnaireGroup;

import java.util.Collection;

/** FHIRQuestionnaire FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaire extends FHIRBaseDomainResource {
    /** Constructs a FHIRQuestionnaire. */
    public FHIRQuestionnaire() {}


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
    private Collection<FHIRIdentifier> identifier;
    private String version;
    private String status;
    private String date;
    private String publisher;
    private Collection<FHIRContactPoint> telecom;
    private Collection<String> subjectType;
    private FHIRQuestionnaireGroup group;

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
     * Returns the version.
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the date.
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the publisher.
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher.
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Returns the telecom.
     * @return the telecom
     */
    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    /**
     * Sets the telecom.
     * @param telecom the telecom
     */
    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    /**
     * Returns the subjectType.
     * @return the subjectType
     */
    public Collection<String> getSubjectType() {
        return subjectType;
    }

    /**
     * Sets the subjectType.
     * @param subjectType the subjectType
     */
    public void setSubjectType(Collection<String> subjectType) {
        this.subjectType = subjectType;
    }

    /**
     * Returns the group.
     * @return the group
     */
    public FHIRQuestionnaireGroup getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group the group
     */
    public void setGroup(FHIRQuestionnaireGroup group) {
        this.group = group;
    }

}
