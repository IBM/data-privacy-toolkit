/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRHumanName {
    /* v1.0.2
    {
        "resourceType" : "HumanName",
        // from Element: extension
        "use" : "<code>", // usual | official | temp | nickname | anonymous | old | maiden
        "text" : "<string>", // Text representation of the full name
        "family" : ["<string>"], // Family name (often called 'Surname')
        "given" : ["<string>"], // Given names (not always 'first'). Includes middle names
        "prefix" : ["<string>"], // Parts that come before the name
        "suffix" : ["<string>"], // Parts that come after the name
        "period" : { Period } // Time period when name was/is in use
    }
     */

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<String> getFamily() {
        return family;
    }

    public void setFamily(Collection<String> family) {
        this.family = family;
    }

    public Collection<String> getGiven() {
        return given;
    }

    public void setGiven(Collection<String> given) {
        this.given = given;
    }

    public Collection<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(Collection<String> prefix) {
        this.prefix = prefix;
    }

    public Collection<String> getSuffix() {
        return suffix;
    }

    public void setSuffix(Collection<String> suffix) {
        this.suffix = suffix;
    }

    public FHIRPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FHIRPeriod period) {
        this.period = period;
    }

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    private Collection<FHIRExtension> extension;
    private String resourceType;
    private String use;
    private String text;
    private Collection<String> family;
    private Collection<String> given;
    private Collection<String> prefix;
    private Collection<String> suffix;
    private FHIRPeriod period;

}
