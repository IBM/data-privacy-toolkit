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
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

/** FHIRGoalOutcome FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRGoalOutcome {
    /** Constructs a FHIRGoalOutcome. */
    public FHIRGoalOutcome() {}


    private FHIRCodeableConcept resultCodeableConcept;
    private FHIRReference resultReference;

    /**
     * Returns the resultCodeableConcept.
     * @return the resultCodeableConcept
     */
    public FHIRCodeableConcept getResultCodeableConcept() {
        return resultCodeableConcept;
    }

    /**
     * Sets the resultCodeableConcept.
     * @param resultCodeableConcept the resultCodeableConcept
     */
    public void setResultCodeableConcept(FHIRCodeableConcept resultCodeableConcept) {
        this.resultCodeableConcept = resultCodeableConcept;
    }

    /**
     * Returns the resultReference.
     * @return the resultReference
     */
    public FHIRReference getResultReference() {
        return resultReference;
    }

    /**
     * Sets the resultReference.
     * @param resultReference the resultReference
     */
    public void setResultReference(FHIRReference resultReference) {
        this.resultReference = resultReference;
    }
}


