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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEventObject {

    private FHIRIdentifier identifier;
    private FHIRReference reference;
    private FHIRCoding type;
    private FHIRCoding role;
    private FHIRCoding lifecycle;
    private Collection<FHIRCoding> securityLabel;
    private String name;
    private String description;
    private String query;
    private Collection<FHIRAuditEventObjectDetail> detail;

    public FHIRIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FHIRIdentifier identifier) {
        this.identifier = identifier;
    }

    public FHIRReference getReference() {
        return reference;
    }

    public void setReference(FHIRReference reference) {
        this.reference = reference;
    }

    public FHIRCoding getType() {
        return type;
    }

    public void setType(FHIRCoding type) {
        this.type = type;
    }

    public FHIRCoding getRole() {
        return role;
    }

    public void setRole(FHIRCoding role) {
        this.role = role;
    }

    public FHIRCoding getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(FHIRCoding lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Collection<FHIRCoding> getSecurityLabel() {
        return securityLabel;
    }

    public void setSecurityLabel(Collection<FHIRCoding> securityLabel) {
        this.securityLabel = securityLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Collection<FHIRAuditEventObjectDetail> getDetail() {
        return detail;
    }

    public void setDetail(Collection<FHIRAuditEventObjectDetail> detail) {
        this.detail = detail;
    }
}


