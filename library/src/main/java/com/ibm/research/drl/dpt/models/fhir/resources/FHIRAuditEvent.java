/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
