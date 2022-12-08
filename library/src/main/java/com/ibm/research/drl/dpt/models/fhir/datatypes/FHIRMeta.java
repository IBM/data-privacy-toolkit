/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMeta {
    /* v1.0.2
    {
  // from Element: extension
  "versionId" : "<id>", // Version specific identifier
  "lastUpdated" : "<instant>", // When the resource version last changed
  "profile" : ["<uri>"], // Profiles this resource claims to conform to
  "security" : [{ Coding }], // Security Labels applied to this resource
  "tag" : [{ Coding }] // Tags applied to this resource
}
     */

    private String versionId;
    private String lastUpdated;
    private Collection<String> profile;
    private Collection<FHIRCoding> security;
    private Collection<FHIRCoding> tag;
}
