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

/** FHIRPeriod FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPeriod {
    /** Constructs a FHIRPeriod. */
    public FHIRPeriod() {}

    private String start;
    private String end;
    private Collection<FHIRExtension> extension;

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
     * Returns the end.
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the end.
     * @param end the end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Returns the start.
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets the start.
     * @param start the start
     */
    public void setStart(String start) {
        this.start = start;
    }


}
