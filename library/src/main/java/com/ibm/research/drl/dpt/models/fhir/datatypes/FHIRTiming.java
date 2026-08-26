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

import java.util.Collection;

/** FHIRTiming FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRTiming {
    /** Constructs a FHIRTiming. */
    public FHIRTiming() {}


    /**
     * Returns the event.
     * @return the event
     */
    public Collection<String> getEvent() {
        return event;
    }

    /**
     * Sets the event.
     * @param event the event
     */
    public void setEvent(Collection<String> event) {
        this.event = event;
    }

    /**
     * Returns the code.
     * @return the code
     */
    public FHIRCodeableConcept getCode() {
        return code;
    }

    /**
     * Sets the code.
     * @param code the code
     */
    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    /**
     * Returns the repeat.
     * @return the repeat
     */
    public FHIRTimingRepeat getRepeat() {
        return repeat;
    }

    /**
     * Sets the repeat.
     * @param repeat the repeat
     */
    public void setRepeat(FHIRTimingRepeat repeat) {
        this.repeat = repeat;
    }

    private Collection<String> event;
    private FHIRCodeableConcept code;
    private FHIRTimingRepeat repeat;


}
