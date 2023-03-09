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
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRAuditEventEvent;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRAuditEventObject;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRAuditEventParticipant;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRAuditEventSource;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAuditEvent extends FHIRBaseDomainResource {
    private String resourceType;

    private FHIRAuditEventEvent event;
    private Collection<FHIRAuditEventParticipant> participant;
    private FHIRAuditEventSource source;
    private Collection<FHIRAuditEventObject> object;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public FHIRAuditEventEvent getEvent() {
        return event;
    }

    public void setEvent(FHIRAuditEventEvent event) {
        this.event = event;
    }

    public Collection<FHIRAuditEventParticipant> getParticipant() {
        return participant;
    }

    public void setParticipant(Collection<FHIRAuditEventParticipant> participant) {
        this.participant = participant;
    }

    public FHIRAuditEventSource getSource() {
        return source;
    }

    public void setSource(FHIRAuditEventSource source) {
        this.source = source;
    }

    public Collection<FHIRAuditEventObject> getObject() {
        return object;
    }

    public void setObject(Collection<FHIRAuditEventObject> object) {
        this.object = object;
    }
}
