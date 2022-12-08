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
public class FHIRTiming {

    public Collection<String> getEvent() {
        return event;
    }

    public void setEvent(Collection<String> event) {
        this.event = event;
    }

    public FHIRCodeableConcept getCode() {
        return code;
    }

    public void setCode(FHIRCodeableConcept code) {
        this.code = code;
    }

    public FHIRTimingRepeat getRepeat() {
        return repeat;
    }

    public void setRepeat(FHIRTimingRepeat repeat) {
        this.repeat = repeat;
    }

    private Collection<String> event;
    private FHIRCodeableConcept code;
    private FHIRTimingRepeat repeat;


}
