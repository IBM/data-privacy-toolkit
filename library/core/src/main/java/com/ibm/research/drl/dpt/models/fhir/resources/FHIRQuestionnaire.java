/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRBaseDomainResource;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;
import com.ibm.research.drl.dpt.models.fhir.subtypes.FHIRQuestionnaireGroup;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRQuestionnaire extends FHIRBaseDomainResource {

    public String getResourceType() {
        return resourceType;
    }

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

    public Collection<FHIRIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Collection<FHIRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Collection<FHIRContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(Collection<FHIRContactPoint> telecom) {
        this.telecom = telecom;
    }

    public Collection<String> getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Collection<String> subjectType) {
        this.subjectType = subjectType;
    }

    public FHIRQuestionnaireGroup getGroup() {
        return group;
    }

    public void setGroup(FHIRQuestionnaireGroup group) {
        this.group = group;
    }

}
