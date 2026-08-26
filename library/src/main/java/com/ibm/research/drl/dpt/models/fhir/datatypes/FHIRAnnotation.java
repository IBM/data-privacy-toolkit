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
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;

import java.util.Collection;

/** FHIRAnnotation FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAnnotation {
    /** Constructs a FHIRAnnotation. */
    public FHIRAnnotation() {}


    /**
     * Returns the extension.
     * @return the extension
     */
    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     * @param extension the extension
     */
    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    /**
     * Returns the authorReference.
     * @return the authorReference
     */
    public FHIRReference getAuthorReference() {
        return authorReference;
    }

    /**
     * Sets the authorReference.
     * @param authorReference the authorReference
     */
    public void setAuthorReference(FHIRReference authorReference) {
        this.authorReference = authorReference;
    }

    /**
     * Returns the authorString.
     * @return the authorString
     */
    public String getAuthorString() {
        return authorString;
    }

    /**
     * Sets the authorString.
     * @param authorString the authorString
     */
    public void setAuthorString(String authorString) {
        this.authorString = authorString;
    }

    /**
     * Returns the time.
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time.
     * @param time the time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    private Collection<FHIRExtension> extension;
    private FHIRReference authorReference;
    private String authorString;
    private String time;
    private String text;

}
