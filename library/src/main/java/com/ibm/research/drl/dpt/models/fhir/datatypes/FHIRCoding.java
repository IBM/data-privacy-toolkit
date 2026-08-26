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

import java.util.Collection;

/** FHIRCoding FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRCoding {
    /** Constructs a FHIRCoding. */
    public FHIRCoding() {}

    /* v1.0.2
    {
  // from Element: extension
  "system" : "<uri>", // Identity of the terminology system
  "version" : "<string>", // Version of the system - if relevant
  "code" : "<code>", // Symbol in syntax defined by the system
  "display" : "<string>", // Representation defined by the system
  "userSelected" : <boolean> // If this coding was chosen directly by the user
}
     */

    private Collection<FHIRExtension> extension;
    private String system;
    private String version;
    private String code;
    private String display;
    private boolean userSelected;

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
     * Returns the system.
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the system.
     * @param system the system
     */
    public void setSystem(String system) {
        this.system = system;
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
     * Returns the code.
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the display.
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the display.
     * @param display the display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * Returns the userSelected.
     * @return the userSelected
     */
    public boolean isUserSelected() {
        return userSelected;
    }

    /**
     * Sets the userSelected.
     * @param userSelected the userSelected
     */
    public void setUserSelected(boolean userSelected) {
        this.userSelected = userSelected;
    }

}
