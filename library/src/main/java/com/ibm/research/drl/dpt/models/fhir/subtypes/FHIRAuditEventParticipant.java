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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

/** FHIRAuditEventParticipant FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventParticipant {
    /** Constructs a FHIRAuditEventParticipant. */
    public FHIRAuditEventParticipant() {}


    private Collection<FHIRCodeableConcept> role;
    private FHIRReference reference;
    private FHIRIdentifier userId;
    private String altId;
    private String name;
    private boolean requestor;
    private FHIRReference location;
    private Collection<String> policy;
    private FHIRCoding media;
    private FHIRAuditEventParticipantNetwork network;
    private Collection<FHIRCoding> purposeOfUse;

    /**
     * Returns the role.
     * @return the role
     */
    public Collection<FHIRCodeableConcept> getRole() {
        return role;
    }

    /**
     * Sets the role.
     * @param role the role
     */
    public void setRole(Collection<FHIRCodeableConcept> role) {
        this.role = role;
    }

    /**
     * Returns the reference.
     * @return the reference
     */
    public FHIRReference getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     * @param reference the reference
     */
    public void setReference(FHIRReference reference) {
        this.reference = reference;
    }

    /**
     * Returns the userId.
     * @return the userId
     */
    public FHIRIdentifier getUserId() {
        return userId;
    }

    /**
     * Sets the userId.
     * @param userId the userId
     */
    public void setUserId(FHIRIdentifier userId) {
        this.userId = userId;
    }

    /**
     * Returns the altId.
     * @return the altId
     */
    public String getAltId() {
        return altId;
    }

    /**
     * Sets the altId.
     * @param altId the altId
     */
    public void setAltId(String altId) {
        this.altId = altId;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the requestor.
     * @return the requestor
     */
    public boolean isRequestor() {
        return requestor;
    }

    /**
     * Sets the requestor.
     * @param requestor the requestor
     */
    public void setRequestor(boolean requestor) {
        this.requestor = requestor;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public FHIRReference getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * @param location the location
     */
    public void setLocation(FHIRReference location) {
        this.location = location;
    }

    /**
     * Returns the policy.
     * @return the policy
     */
    public Collection<String> getPolicy() {
        return policy;
    }

    /**
     * Sets the policy.
     * @param policy the policy
     */
    public void setPolicy(Collection<String> policy) {
        this.policy = policy;
    }

    /**
     * Returns the media.
     * @return the media
     */
    public FHIRCoding getMedia() {
        return media;
    }

    /**
     * Sets the media.
     * @param media the media
     */
    public void setMedia(FHIRCoding media) {
        this.media = media;
    }

    /**
     * Returns the network.
     * @return the network
     */
    public FHIRAuditEventParticipantNetwork getNetwork() {
        return network;
    }

    /**
     * Sets the network.
     * @param network the network
     */
    public void setNetwork(FHIRAuditEventParticipantNetwork network) {
        this.network = network;
    }

    /**
     * Returns the purposeOfUse.
     * @return the purposeOfUse
     */
    public Collection<FHIRCoding> getPurposeOfUse() {
        return purposeOfUse;
    }

    /**
     * Sets the purposeOfUse.
     * @param purposeOfUse the purposeOfUse
     */
    public void setPurposeOfUse(Collection<FHIRCoding> purposeOfUse) {
        this.purposeOfUse = purposeOfUse;
    }
}
